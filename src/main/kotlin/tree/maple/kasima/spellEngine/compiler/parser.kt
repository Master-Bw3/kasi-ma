package tree.maple.kasima.spellEngine.compiler

import net.minecraft.text.Text
import net.minecraft.util.Identifier
import tree.maple.kasima.KasiMa
import java.util.LinkedList


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
    data class Apply(val function: UntypedIRNode, val argument: UntypedIRNode, val offset: UInt) : UntypedIRNode()

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
    val offsets = LinkedList<UInt>()
    var offset = 0u

    for (node in members) {
        if (node is ASTNode.Gap) {
            offset += 1u
        } else {
            offsets.push(offset)
        }
    }

    val filtered = members.filter { it !is ASTNode.Gap }

    var acc = constructUntypedIR(filtered.last())

    for (i in filtered.size - 2 downTo 0) {
        val node = filtered[i]
        acc = UntypedIRNode.Apply(constructUntypedIR(node), acc, offsets.pop())
    }

    return acc
}
