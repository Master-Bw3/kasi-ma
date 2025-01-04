package tree.maple.kasima.spellEngine.compiler

import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.javafp.parsecj.*
import org.javafp.parsecj.input.Input
import tree.maple.kasima.KasiMa
import tree.maple.kasima.api.registry.RuneBlockTokenRegistry
import tree.maple.kasima.spellEngine.operators.Operator
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.util.function.Predicate
import kotlin.collections.ArrayDeque


sealed class Token {
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
            .many1()
            .map { ASTNode.ApplyChain(it.stream().toList()) }

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
            )

        )
    }
}


sealed class ASTNode {
    data class ApplyChain(val members: List<ASTNode>) : ASTNode()

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

        is ASTNode.Compose -> TODO()
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
    abstract val type: Type.Function

    data class Apply(
        val function: TypedIRNode,
        val argument: TypedIRNode,
        val argOffset: Int,
        override val type: Type.Function
    ) : TypedIRNode()

    data class Operator(val identifier: Identifier, override val type: Type.Function) : TypedIRNode()
}

sealed class Constraint {
    data class Equality(val t1: Type, val t2: Type) : Constraint()
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

                if (node.argOffset < inferredFunction.type.signature.size - 1) {
                    val newSignature = inferredFunction.type.signature.toMutableList()
                    newSignature.removeAt(node.argOffset)

                    var param = inferredFunction.type.signature[node.argOffset]
                    if (param !is Type.Function) param = Type.Function(listOf(param))

                    typeConstraints += Constraint.Equality(
                        param,
                        inferredArg.type
                    )

                    TypedIRNode.Apply(inferredFunction, inferredArg, node.argOffset, Type.Function(newSignature))
                } else {
                    throw CompilerError.TypeError("invalid apply arguments")
                }
            }

            is UntypedIRNode.Operator -> {
                val opType = RuneBlockTokenRegistry[node.identifier]!!.operator!!.type

                val inferredSignature = inferSignature(opType.signature)

                TypedIRNode.Operator(
                    node.identifier,
                    Type.Function(inferredSignature)
                )
            }
        }

    private fun inferSignature(signature: List<Type>): List<Type> {
        val env = mutableMapOf<UInt, Type>()
        return signature.map { inferType(it, env) }
    }

    private fun inferType(type: Type, environment: MutableMap<UInt, Type>): Type {
        return when (type) {
            is Type.Named -> Type.Named(type.name, type.rawType, type.genericArgs.map { inferType(it, environment) })

            is Type.Function -> {
                val inferred = inferSignature(type.signature)

                Type.Function(inferred)
            }

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
            if (t1.signature.size != t2.signature.size)
                throw CompilerError.TypeError("Signature mismatch: ${t1.signature} vs. ${t2.signature}")
            t1.signature.zip(t2.signature).forEach { (t1, t2) -> unify(t1, t2) }

        } else throw CompilerError.TypeError("Type mismatch: $t1 vs. $t2")
    }

    private fun substituteNode(node: TypedIRNode): TypedIRNode {
        return when (node) {
            is TypedIRNode.Apply -> TypedIRNode.Apply(
                substituteNode(node.function),
                substituteNode(node.argument),
                node.argOffset,
                substituteFunctionType(node.type)
            )

            is TypedIRNode.Operator -> TypedIRNode.Operator(
                node.identifier,
                substituteFunctionType(node.type)
            )
        }
    }

    private fun substituteType(type: Type): Type {
        return when (type) {
            is Type.Function -> substituteFunctionType(type)
            is Type.Generic -> type
            is Type.Named -> Type.Named(type.name, type.rawType, type.genericArgs.map { substituteType(it) })
            is Type.Var -> substitutions[type.index]
        }
    }

    private fun substituteFunctionType(type: Type.Function) =
        Type.Function(type.signature.map { substituteType(it) })

    companion object {
        fun inferNode(node: UntypedIRNode) = InferenceEnv(node)
    }

}

fun compileToMethodHandle(node: TypedIRNode): MethodHandle {
    return when (node) {
        is TypedIRNode.Apply -> compileApplyToMethodHandle(node)
        is TypedIRNode.Operator -> RuneBlockTokenRegistry[node.identifier]!!.operator!!.getHandle(node.type)
    }
}

fun compileApplyToMethodHandle(node: TypedIRNode.Apply): MethodHandle {
    val functionHandle = compileToMethodHandle(node.function)
    val argumentHandle = compileToMethodHandle(node.argument)
    val filter =
        if (node.argument.type.signature.size == 1) argumentHandle
        else MethodHandles.constant(MethodHandle::class.java, argumentHandle)

    return MethodHandles.collectArguments(functionHandle, node.argOffset, filter)
}

fun compileToFunction(node: TypedIRNode): Operator =
    object : Operator() {
        val handle = compileToMethodHandle(node)

        override val type = node.type

        override fun getHandle(castedType: Type.Function): MethodHandle {
            if (this.type != castedType) throw IllegalArgumentException()

            return handle
        }
    }

fun compileAndRun(program: Collection<Token>): Any {
    val function = compileToFunction(
        InferenceEnv.inferNode(constructUntypedIR(parse(program.toTypedArray()))).solveConstraints().substitute()
    )

    return if (function.type.signature.size > 1) {
        function
    } else {
        function.getHandle(function.type).invoke()
    }
}

// ((add~1)~add)~1