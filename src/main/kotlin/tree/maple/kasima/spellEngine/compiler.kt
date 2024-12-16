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
        override val arguments: List<Type<*>> = handle.type().parameterList().map { Type.fromRawType(it.kotlin) }

        override val returnType: Type<*> = Type.fromRawType(handle.type().returnType().kotlin)

        override val handle: MethodHandle = handle
    }
}

data class TypeError(val node: ASTNode, val expected: Type<*>, val actual: Type<*>?) : Throwable()

fun typeCheck(node: ASTNode) : Type<*> {
    val expectedArgTypes = node.rune.arguments
    val argTypes = node.args.map { typeCheck(it) }

    expectedArgTypes.forEachIndexed { i, expected ->
        val actual = argTypes.getOrNull(i)

        if (expected != actual) {
            throw TypeError(node, expected, actual)
        }
    }

    return node.rune.returnType
}

