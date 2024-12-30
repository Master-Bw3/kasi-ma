package tree.maple.kasima.spellEngine.operators

import tree.maple.kasima.spellEngine.compiler.Type
import java.lang.invoke.MethodHandle

abstract class SimpleOperator : Operator() {

    private val handle: MethodHandle by lazy { super.getHandle(this.type) }

    override fun getHandle(castedType: Type.Function): MethodHandle {
        if (this.type != castedType) throw IllegalArgumentException()

        return handle
    }
}