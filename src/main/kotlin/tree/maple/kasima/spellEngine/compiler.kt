package tree.maple.kasima.spellEngine

import tree.maple.kasima.spellEngine.runes.Rune
import tree.maple.kasima.spellEngine.types.Type
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles

data class ASTNode(val rune: Rune, val args: List<ASTNode>)


fun compileToHandle(node: ASTNode): MethodHandle {
    var handle = node.rune.handle

    for (argNode in node.args) {
        val argHandle = compileToHandle(argNode)

        handle = MethodHandles.collectArguments(handle, 0, argHandle)
    }

    return handle
}

fun compile(node: ASTNode) : Rune {
    val handle = compileToHandle(node)


    return object : Rune() {
        override val arguments: List<Type<*>> = handle.type().parameterList().map { Type.of(it) }

        override val returnType: Type<*> = Type.of(handle.type().returnType())

        override val handle: MethodHandle = handle
    }
}