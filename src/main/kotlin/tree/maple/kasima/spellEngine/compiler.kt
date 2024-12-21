package tree.maple.kasima.spellEngine

import net.minecraft.util.Identifier
import tree.maple.kasima.api.registry.RuneRegistry
import tree.maple.kasima.spellEngine.types.SpellFunction
import tree.maple.kasima.spellEngine.types.SpellFunctionType
import tree.maple.kasima.spellEngine.types.Type
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType


sealed class ValidationState {
    data object Validated : ValidationState()

    data object NotValidated : ValidationState()
}

data class TypeError(val node: ASTNode<*>, val actual: List<Type<*>?>) : Throwable()

sealed class ASTNode<T : ValidationState> {

    class Operator<T : ValidationState>(val operator: Identifier, val args: List<ASTNode<T>>) : ASTNode<T>()

    class Capture<T : ValidationState>(val operator: Identifier, val args: List<ASTNode<T>?>) : ASTNode<T>()

    class Apply<T : ValidationState>(val to: ASTNode<T>, val args: List<ASTNode<T>>) : ASTNode<T>()


    /**
     * checks types and reorders arguments
     */
    fun validate(): ASTNode<ValidationState.Validated> {
        return when (this) {
            is Capture<*> -> {
                val opRef = (RuneRegistry.get(operator)!!.rune as Rune.Function).function

                val validated = this.args.map { it?.validate() }

                val expectedArgTypes = opRef.arguments
                val actualArgTypes = validated.map { it?.let { getReturnType(it) } }

                for ((actual, expect) in actualArgTypes.zip(expectedArgTypes)) {
                    if (actual != null && actual != expect) {
                        throw TypeError(this, actualArgTypes)
                    }
                }

                Capture(this.operator, validated)
            }

            is Operator<*> -> {
                val opRef = (RuneRegistry.get(operator)!!.rune as Rune.Function).function

                val validated = this.args.map { it.validate() }

                val expectedArgTypes = opRef.arguments
                val actualArgTypes = validated.map { getReturnType(it) }

                if (expectedArgTypes != actualArgTypes) {
                    throw TypeError(this, actualArgTypes)
                }

                Operator(this.operator, validated)
            }

            is Apply<*> -> {
                val validatedTarget = this.to.validate()
                val targetType = getReturnType(validatedTarget)
                if (targetType is SpellFunctionType) {
                    val validated = this.args.map { it.validate() }

                    val expectedArgTypes = targetType.arguments
                    val actualArgTypes = validated.map { getReturnType(it) }

                    if (expectedArgTypes != actualArgTypes) {
                        throw TypeError(this, actualArgTypes)
                    }

                    return Apply(validatedTarget, validated)
                } else {
                    //TODO: fix error
                    throw TypeError(this, listOf())
                }
            }
        }
    }
}

fun getReturnType(node: ASTNode<ValidationState.Validated>): Type<*> {
    return when (node) {
        is ASTNode.Capture<*> -> object : SpellFunction() {
            val function = (RuneRegistry.get(node.operator)!!.rune as Rune.Function).function

            override val arguments: List<Type<*>>
                get() = node.args.zip(function.arguments).filter { (a, _) -> a == null }.map { (_, b) -> b }

            override val returnType: Type<*>
                get() = function.returnType
        }.type

        is ASTNode.Operator<*> -> (RuneRegistry.get(node.operator)!!.rune as Rune.Function).function.returnType

        is ASTNode.Apply<ValidationState.Validated> -> getReturnType(node.to)
    }
}

fun compile(node: ASTNode<ValidationState.Validated>): SpellFunction {
    val handle = compileToHandle(node)


    return object : SpellFunction() {
        override val arguments: List<Type<*>> = listOf()

        override val returnType: Type<*> = getReturnType(node)

        override val handle: MethodHandle = handle
    }
}

fun compileToHandle(node: ASTNode<ValidationState.Validated>): MethodHandle {
    return when (node) {
        is ASTNode.Capture<ValidationState.Validated> -> {
            val function = (RuneRegistry.get(node.operator)!!.rune as Rune.Function).function
            val argHandles = node.args.map { it?.let { compileToHandle(it) } }
            var handle = function.handle

            MethodHandles.constant(SpellFunction::class.java, object : SpellFunction() {

                override val arguments: List<Type<*>>
                    get() = node.args.zip(function.arguments).filter { (a, _) -> a == null }.map { (_, b) -> b }

                override val returnType: Type<*>
                    get() = function.returnType

                fun apply(values: List<Any>) {
                    val valuesStream = values.iterator()
                    argHandles.map { it?.invoke() ?: valuesStream.next() }
                }

                override val handle: MethodHandle
                    get() {
                        val lookup = MethodHandles.lookup()
                        val methodType =
                            MethodType.methodType(returnType.rawType.java, arguments.map { it.rawType.java })
                        return lookup.findVirtual(
                            this::class.java, "apply", methodType
                        )
                    }
            })
        }

        is ASTNode.Operator<ValidationState.Validated> -> {
            val function = (RuneRegistry.get(node.operator)!!.rune as Rune.Function).function

            var handle = function.handle

            for (argNode in node.args) {
                val argHandle = compileToHandle(argNode)

                handle = MethodHandles.collectArguments(handle, 0, argHandle)
            }

            handle
        }

        is ASTNode.Apply<ValidationState.Validated> -> {
            var handle = compileToHandle(node.to)

            for (argNode in node.args) {
                val argHandle = compileToHandle(argNode)

                handle = MethodHandles.collectArguments(handle, 0, argHandle)
            }

            handle
        }

    }
}





