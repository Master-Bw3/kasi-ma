package tree.maple.kasima.spellEngine.compiler

import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.javafp.parsecj.*
import org.javafp.parsecj.input.Input
import tree.maple.kasima.KasiMa
import tree.maple.kasima.api.registry.OperatorRegistry
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.util.function.Predicate


sealed class Token {
    data class Constant(val value: Any, val type: Type) : Token()

    data class Operator(val operator: Identifier) : Token()

    data class CombinatorSymbol(val symbol: Identifier) : Token()

    data object Gap : Token()

    data object Compose : Token()

    data object StartGroup : Token()

    data object EndGroup : Token()
}

object KasiMaParser {
    val expressionParser: Parser.Ref<Token, ASTNode> = Parser.ref()

    val combinatorExprParser: Parser<Token, ASTNode> =
        Combinators.satisfy(Predicate<Token> { it is Token.CombinatorSymbol })
            .map { (it as Token.CombinatorSymbol).symbol }
            .many1()
            .map { ASTNode.CombinatorExpr(it.stream().toList()) }

    val operatorParser: Parser<Token, ASTNode> =
        Combinators.satisfy(Predicate<Token> { it is Token.Operator })
            .map { ASTNode.Operator((it as Token.Operator).operator) }

    val constantParser: Parser<Token, ASTNode> =
        Combinators.satisfy(Predicate<Token> { it is Token.Constant })
            .map { token -> (token as Token.Constant).let { ASTNode.Constant(it.value, it.type) } }

    val gapParser: Parser<Token, ASTNode> =
        Combinators.satisfy(Predicate<Token> { it is Token.Gap })
            .map { ASTNode.Gap }

    val groupParser: Parser<Token, ASTNode> =
        Combinators.satisfy(Predicate<Token> { it is Token.StartGroup })
            .then(
                expressionParser
                    .bind { expr ->
                        Combinators.satisfy(Predicate<Token> { it is Token.EndGroup })
                            .then(Combinators.retn(expr))
                    })

    val applyParser: Parser<Token, ASTNode> =
        Combinators.choice(groupParser, combinatorExprParser, operatorParser, gapParser)
            .bind { first ->
                Combinators.choice(groupParser, combinatorExprParser, operatorParser, gapParser, constantParser)
                    .many()
                    .map { ASTNode.ApplyChain(listOf(first) + it.stream().toList()) }
            }


    val composeParser: Parser<Token, ASTNode> =
        Combinators.attempt(
            Combinators.choice(applyParser, groupParser, combinatorExprParser)
                .bind { lhs ->
                    Combinators.satisfy(Predicate<Token> { it is Token.Compose }).then(
                        expressionParser.bind { rhs -> Combinators.retn(ASTNode.Compose(lhs, rhs)) }
                    )
                }
        )

    val parser: Parser<Token, ASTNode> =
        expressionParser.bind { expr -> Combinators.eof<Token>().then(Combinators.retn(expr)) }

    init {
        expressionParser.set(
            Combinators.choice(
                composeParser,
                applyParser,
                groupParser,
                combinatorExprParser,
                constantParser,
            )
        )
    }
}


sealed class ASTNode {
    data class ApplyChain(val members: List<ASTNode>) : ASTNode()

    data class Constant(val value: Any, val type: Type) : ASTNode()

    data class Operator(val operator: Identifier) : ASTNode()

    data class Compose(val rhs: ASTNode, val lhs: ASTNode) : ASTNode()

    data class CombinatorExpr(val expression: List<Identifier>) : ASTNode()

    data object Gap : ASTNode()
}


sealed class CompilerError(msg: String?) : Throwable(msg) {
    class UnknownError : CompilerError(null)

    data class SyntaxError(val error: Text) : CompilerError(error.string)

    class TypeError(msg: String) : CompilerError(msg)
}

fun parse(tokens: Array<Token>): ASTNode = KasiMaParser.expressionParser.parse(Input.of(tokens)).result


sealed class UntypedIRNode {
    data class Apply(val function: UntypedIRNode, val argument: UntypedIRNode, val argOffset: Int) : UntypedIRNode()

    data class Operator(val identifier: Identifier) : UntypedIRNode()

    data class Constant(val value: Any, val type: Type) : UntypedIRNode()
}

fun constructUntypedIR(node: ASTNode): UntypedIRNode {
    return when (node) {
        is ASTNode.Operator -> UntypedIRNode.Operator(node.operator)
        is ASTNode.ApplyChain -> chainApply(node.members)
        ASTNode.Gap -> throw CompilerError.SyntaxError(
            Text.translatable(
                KasiMa.id("illegal_start.gap").toTranslationKey()
            )
        )

        is ASTNode.Compose -> UntypedIRNode.Apply(
            UntypedIRNode.Apply(
                UntypedIRNode.Operator(KasiMa.id("compose")),
                constructUntypedIR(node.lhs),
                0
            ), constructUntypedIR(node.rhs), 0
        )

        is ASTNode.Constant -> UntypedIRNode.Constant(node.value, node.type)


        is ASTNode.CombinatorExpr -> TODO()
    }
}


fun chainApply(members: List<ASTNode>): UntypedIRNode {
    var offset = 0
    var acc = constructUntypedIR(members.first())
    for (i in 1..members.indexOfLast { it !is ASTNode.Gap }) {
        val node = members[i]
        if (node is ASTNode.Gap) {
            offset += 1
        } else {
            acc = UntypedIRNode.Apply(acc, constructUntypedIR(node), offset)
        }
    }

    return acc
}

sealed class TypedIRNode {
    abstract val type: Type

    data class Apply(
        val function: TypedIRNode,
        val argument: TypedIRNode,
        val argOffset: Int,
        override val type: Type
    ) : TypedIRNode()

    data class Operator(val identifier: Identifier, override val type: Type.Function) : TypedIRNode()

    data class Constant(val value: Any, override val type: Type) : TypedIRNode()

}

sealed class Constraint {
    data class Equality(val t1: Type, val t2: Type) : Constraint() {
        override fun toString(): String {
            return "$t1 == $t2"
        }
    }
}

class InferenceEnv private constructor(node: UntypedIRNode) {
    private val typeConstraints: MutableList<Constraint> = mutableListOf()
    private val substitutions: MutableList<Type> = mutableListOf()
    private var node: TypedIRNode

    init {
        this.node = inferNode(node)
    }

    fun substitute(): TypedIRNode {
        node = substituteNode(node)
        return node
    }

    fun solveConstraints(): InferenceEnv {
        typeConstraints.map { it as Constraint.Equality }.forEach { (t1, t2) -> unify(t1, t2) }
        typeConstraints.clear()
        return this
    }

    private fun createTypeVar(): Type.Var {
        val variable = Type.Var(substitutions.size)
        substitutions.add(variable)
        return variable
    }

    private fun inferNode(node: UntypedIRNode): TypedIRNode =
        when (node) {
            is UntypedIRNode.Apply -> {
                val inferredFunction = inferNode(node.function)
                val inferredArg = inferNode(node.argument)
                var functionType = inferredFunction.type


                if (functionType is Type.Var) {
                    val newFunctionType = Type.Function(List(node.argOffset + 2) { createTypeVar() })

                    typeConstraints += Constraint.Equality(
                        newFunctionType,
                        functionType,
                    )

                    functionType = newFunctionType
                }

                if (functionType !is Type.Function) {
                    throw CompilerError.TypeError("left argument of apply takes no inputs")
                }

                if (node.argOffset < functionType.length - 1) {
                    val newType = functionType.removeAt(node.argOffset)
                    val param = functionType[node.argOffset]!!

                    typeConstraints += Constraint.Equality(
                        param,
                        inferredArg.type
                    )

                    TypedIRNode.Apply(inferredFunction, inferredArg, node.argOffset, newType)
                } else {
                    throw CompilerError.TypeError("invalid apply arguments")
                }


            }

            is UntypedIRNode.Operator -> {
                val opType = OperatorRegistry[node.identifier]!!.type

                val inferredSignature = inferSignature(opType)

                TypedIRNode.Operator(
                    node.identifier,
                    inferredSignature
                )
            }

            is UntypedIRNode.Constant -> TypedIRNode.Constant(node.value, node.type)
        }

    private fun inferSignature(
        signature: Type.Function,
        environment: MutableMap<UInt, Type> = mutableMapOf()
    ): Type.Function {
        return signature.map { inferType(it, environment) }
    }

    private fun inferType(type: Type, environment: MutableMap<UInt, Type>): Type {
        return when (type) {
            is Type.Named -> Type.Named(type.name, type.rawType, type.genericArgs.map { inferType(it, environment) })

            is Type.Function -> inferSignature(type, environment)

            is Type.Generic -> environment.getOrPut(type.id, ::createTypeVar)

            is Type.Var -> type
        }
    }

    private fun unify(t1: Type, t2: Type) {
        if (t1 is Type.Var && substitutions[t1.index] != t1) {
            unify(substitutions[t1.index], t2)

        } else if (t2 is Type.Var && substitutions[t2.index] != t2) {
            unify(t1, substitutions[t2.index])

        } else if (t1 is Type.Var) {
            substitutions[t1.index] = t2

        } else if (t2 is Type.Var) {
            substitutions[t2.index] = t1

        } else if (t1 is Type.Named && t2 is Type.Named && t1.name == t2.name) {
            if (t1.genericArgs.size != t2.genericArgs.size)
                throw CompilerError.TypeError("Generics mismatch: ${t1.genericArgs} vs. ${t2.genericArgs}")
            t1.genericArgs.zip(t2.genericArgs).forEach { (t1, t2) -> unify(t1, t2) }

        } else if (t1 is Type.Function && t2 is Type.Function) {
            unifyFunction(t1, t2)

        } else throw CompilerError.TypeError("Type mismatch: $t1 vs. $t2")
    }

    private fun unifyFunction(t1: Type.Function, t2: Type.Function) {
        val (t1Head: Type, t1Tail: Type) = t1.left to t1.right

        val (t2Head: Type, t2Tail: Type) = t2.left to t2.right

        unify(t1Head, t2Head)
        unify(t1Tail, t2Tail)
    }

    private fun substituteNode(node: TypedIRNode): TypedIRNode {
        return when (node) {
            is TypedIRNode.Apply -> TypedIRNode.Apply(
                substituteNode(node.function),
                substituteNode(node.argument),
                node.argOffset,
                substituteType(node.type)
            )

            is TypedIRNode.Operator -> TypedIRNode.Operator(
                node.identifier,
                substituteFunction(node.type)
            )

            is TypedIRNode.Constant -> node
        }
    }

    private fun substituteType(type: Type): Type {
        return when (type) {
            is Type.Function -> substituteFunction(type)
            is Type.Generic -> type
            is Type.Named -> Type.Named(type.name, type.rawType, type.genericArgs.map { substituteType(it) })
            is Type.Var -> substitutions[type.index]
        }
    }

    private fun substituteFunction(type: Type.Function): Type.Function =
        type.map { substituteType(it) }

    companion object {
        fun inferNode(node: UntypedIRNode) = InferenceEnv(node)
    }

}

fun compileToMethodHandle(node: TypedIRNode): MethodHandle {
    return when (node) {
        is TypedIRNode.Apply -> compileApplyToMethodHandle(node)
        is TypedIRNode.Operator -> OperatorRegistry[node.identifier]!!.getHandle(node.type)
        is TypedIRNode.Constant -> MethodHandles.constant(getRawType(node.type), node.value)
    }
}

fun compileApplyToMethodHandle(node: TypedIRNode.Apply): MethodHandle {
    val functionHandle = compileToMethodHandle(node.function)

    var argumentHandle = compileToMethodHandle(node.argument)
    if (node.argument.type is Type.Function) argumentHandle =
        MethodHandles.constant(MethodHandle::class.java, argumentHandle)

    val handle = MethodHandles.collectArguments(functionHandle, node.argOffset, argumentHandle)


    return if (handle.type().parameterCount() == 0 && handle.type().returnType() == MethodHandle::class.java) {
        val type = node.type
        val methodType = when (type) {
            is Type.Function -> {
                val signature = type.toList()
                MethodType.methodType(
                    getRawType(signature.last()),
                    signature.dropLast(1).map { getRawType(it) }
                )
            }

            else -> MethodType.methodType(getRawType(type))
        }

        val invoker = MethodHandles.invoker(methodType)

        MethodHandles.collectArguments(invoker, 0, handle)
    } else {
        handle
    }
}


fun compileAndRun(program: Collection<Token>): Any {
    val node = InferenceEnv.inferNode(constructUntypedIR(parse(program.toTypedArray()))).solveConstraints().substitute()
    val handle = compileToMethodHandle(node)

    return if (node.type is Type.Function) {
        handle
    } else {
        handle.invoke()
    }
}


fun <T> MethodHandle.bind(x: T): MethodHandle = MethodHandles.insertArguments(this, 0, x)

fun MethodHandle.flatten(): Any =
    if (this.type().parameterCount() == 0) this.invoke()
    else this



//(n -> n) -> n -> a
//n -> n -> n