package tree.maple.kasima.spellEngine.compiler

import net.minecraft.text.Text
import net.minecraft.util.Identifier
import tree.maple.kasima.KasiMa
import tree.maple.kasima.api.registry.RuneBlockTokenRegistry
import tree.maple.kasima.spellEngine.operators.Operator
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import kotlin.collections.ArrayDeque


sealed class Token {
    data class Operator(val operator: Identifier) : Token()

    data object Gap : Token()

    data object StartGroup : Token()

    data object EndGroup : Token()
}

sealed class ASTNode {
    data class Group(val members: List<ASTNode>) : ASTNode()

    data class Operator(val operator: Identifier) : ASTNode()

    data object Gap : ASTNode()
}


sealed class CompilerError(msg: String?) : Throwable(msg) {
    class UnknownError : CompilerError(null)

    data class SyntaxError(val error: Text) : CompilerError(error.string)

    class TypeError(msg: String) : CompilerError(msg)
}

fun parse(tokens: ArrayDeque<Token>): ASTNode {
    return when (val firstToken = tokens.removeFirst()) {

        Token.StartGroup -> ASTNode.Group(parseParens(tokens))

        Token.Gap -> ASTNode.Gap

        is Token.Operator -> ASTNode.Operator(firstToken.operator)

        else -> throw CompilerError.UnknownError()
    }
}

fun parseParens(tokens: ArrayDeque<Token>): List<ASTNode> {
    val args = mutableListOf<ASTNode>()

    while (tokens.first() != Token.EndGroup) {
        args.add(parse(tokens))
    }

    tokens.removeFirst()
    return args
}

sealed class UntypedIRNode {
    data class Apply(val function: UntypedIRNode, val argument: UntypedIRNode, val argOffset: Int) : UntypedIRNode()

    data class Operator(val identifier: Identifier) : UntypedIRNode()
}

fun constructUntypedIR(node: ASTNode): UntypedIRNode {
    return when (node) {
        is ASTNode.Operator -> UntypedIRNode.Operator(node.operator)
        is ASTNode.Group -> chainApply(node.members)
        ASTNode.Gap -> throw CompilerError.SyntaxError(
            Text.translatable(
                KasiMa.id("illegal_start.gap").toTranslationKey()
            )
        )
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

    fun substitute(): TypedIRNode = substituteNode(node)

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

                if (node.argOffset < inferredFunction.type.signature.size - 1 && inferredArg.type.signature.size == 1) {
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
            is Type.Named -> Type.Named(type.name, type.rawType, type.genericArgs.map { inferType(it, mutableMapOf()) })

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
        is TypedIRNode.Operator -> RuneBlockTokenRegistry[node.identifier]!!.operator!!.handle
    }
}

fun compileApplyToMethodHandle(node: TypedIRNode.Apply): MethodHandle {
    val functionHandle = compileToMethodHandle(node.function)
    val argumentHandle = compileToMethodHandle(node.argument)

    return MethodHandles.collectArguments(functionHandle, node.argOffset, argumentHandle)
}

fun compileToFunction(node: TypedIRNode): Operator =
    object : Operator() {
        override val type = node.type

        override val handle: MethodHandle = compileToMethodHandle(node)
    }

fun compileAndRun(program: Collection<Token>): Any {
    val function = compileToFunction(
        InferenceEnv.inferNode(constructUntypedIR(parse(ArrayDeque(program)))).solveConstraints().substitute()
    )

    return if (function.type.signature.size > 1) {
        function
    } else {
        function.handle.invoke()
    }
}

