package tree.maple.kasima.spellEngine

import com.google.common.collect.HashMultiset
import com.google.common.collect.ImmutableMultiset
import com.google.common.collect.Multiset
import tree.maple.kasima.spellEngine.runes.Rune
import tree.maple.kasima.spellEngine.types.Type
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles


sealed class ValidationState {
    class Validated : ValidationState()

    class NotValidated : ValidationState()
}

data class TypeError(val node: ASTNode<*>, val actual: Multiset<Type<*>>?) : Throwable()

data class ASTNode<T : ValidationState>(val rune: Rune, val args: List<ASTNode<T>>) {


    /**
     * checks types and reorders arguments
     */
    fun validate(): ASTNode<ValidationState.Validated> {
        val validated = this.args.map { it.validate() }

        val expectedArgTypes = this.rune.arguments
        val actualArgTypes = validated.mapTo(HashMultiset.create()) { it.rune.returnType }

        if (ImmutableMultiset.copyOf(expectedArgTypes) != actualArgTypes) {
            throw TypeError(this, actualArgTypes)
        }

        return ASTNode(this.rune, reorderArgs(validated, expectedArgTypes))
    }

}

fun <T : ValidationState> reorderArgs(listToReorder: List<ASTNode<T>>, referenceList: List<Type<*>>) : List<ASTNode<T>> {
    val indexMap: MutableMap<Type<*>, Int> = HashMap()
    referenceList.indices.forEach { i ->
        indexMap[referenceList[i]] = i
    }

    return listToReorder.sortedWith(Comparator.comparingInt { node: ASTNode<*> -> indexMap[node.rune.returnType]!! })
}

fun compile(node: ASTNode<ValidationState.Validated>): Rune {
    val handle = compileToHandle(node)


    return object : Rune(0U) {
        override val arguments: List<Type<*>> = handle.type().parameterList().map { Type.fromRawType(it.kotlin) }

        override val returnType: Type<*> = Type.fromRawType(handle.type().returnType().kotlin)

        override val handle: MethodHandle = handle
    }
}

fun compileToHandle(node: ASTNode<ValidationState.Validated>): MethodHandle {
    var handle = node.rune.handle

    for (argNode in node.args) {
        val argHandle = compileToHandle(argNode)

        handle = MethodHandles.collectArguments(handle, 0, argHandle)
    }

    return handle
}





