package tree.maple.kasima.spellEngine.compiler

import net.minecraft.text.Text
import net.minecraft.util.Identifier
import tree.maple.kasima.KasiMa
import tree.maple.kasima.api.registry.RuneBlockTokenRegistry
import tree.maple.kasima.spellEngine.types.SpellFunction
import tree.maple.kasima.spellEngine.types.Type
import tree.maple.kasima.spellEngine.types.Value
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.util.SequencedCollection
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

    class TypeError : CompilerError(null)
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
    abstract val signature: List<Type<*>>

    data class Apply(
        val function: TypedIRNode,
        val argument: TypedIRNode,
        val argOffset: Int,
        override val signature: List<Type<*>>
    ) : TypedIRNode()

    data class Operator(val identifier: Identifier, override val signature: List<Type<*>>) : TypedIRNode()
}

fun typeCheck(irNode: UntypedIRNode): TypedIRNode {
    return when (irNode) {
        is UntypedIRNode.Apply -> typeCheckApply(irNode)
        is UntypedIRNode.Operator -> typeCheckOperator(irNode)
    }
}

fun typeCheckApply(applyNode: UntypedIRNode.Apply): TypedIRNode {
    val typeCheckedFunction = typeCheck(applyNode.function)
    val typeCheckedArg = typeCheck(applyNode.argument)

    if (applyNode.argOffset < typeCheckedFunction.signature.size - 1 && typeCheckedArg.signature.size == 1
        && typeCheckedArg.signature.first() == typeCheckedFunction.signature[applyNode.argOffset]
    ) {
        val newSignature = typeCheckedFunction.signature.toMutableList()
        newSignature.removeAt(applyNode.argOffset)

        return TypedIRNode.Apply(typeCheckedFunction, typeCheckedArg, applyNode.argOffset, newSignature)
    } else {
        throw CompilerError.TypeError()
    }
}

fun typeCheckOperator(operatorNode: UntypedIRNode.Operator): TypedIRNode {
    val operator = RuneBlockTokenRegistry[operatorNode.identifier]!!.function!!

    return TypedIRNode.Operator(operatorNode.identifier, operator.signature)
}

fun compileToMethodHandle(node: TypedIRNode): MethodHandle {
    return when (node) {
        is TypedIRNode.Apply -> compileApplyToMethodHandle(node)
        is TypedIRNode.Operator -> RuneBlockTokenRegistry[node.identifier]!!.function!!.handle
    }
}

fun compileApplyToMethodHandle(node: TypedIRNode.Apply): MethodHandle {
    val functionHandle = compileToMethodHandle(node.function)
    val argumentHandle = compileToMethodHandle(node.argument)

    return MethodHandles.collectArguments(functionHandle, node.argOffset, argumentHandle)
}

fun compileToFunction(node: TypedIRNode): SpellFunction =
    object : SpellFunction() {
        override val signature: List<Type<*>> = node.signature

        override val handle: MethodHandle = compileToMethodHandle(node)
    }

fun compileAndRun(program: Collection<Token>): Value {
    val function = compileToFunction(typeCheck(constructUntypedIR(parse(ArrayDeque(program)))))

    return if (function.signature.size > 1) {
        function
    } else {
        function.signature[0].construct(
            function.handle.invoke()
        )
    }
}