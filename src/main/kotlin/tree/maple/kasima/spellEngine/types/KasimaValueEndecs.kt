package tree.maple.kasima.spellEngine.types


import io.wispforest.endec.Endec
import tree.maple.kasima.KasiMa
import tree.maple.kasima.api.registry.ValueEndecRegistry


object KasimaValueEndecs {

    //val NUMBER: Type<NumberValue> = register(NumberValue.TYPE, "number")
    //val BOOLEAN: Type<BooleanValue> = register(BooleanValue.TYPE, "boolean")
    //val FUNCTION : Type<SpellFunction> = register(SpellFunction.TYPE, "function")

    private fun <T : Value> register(endec: Endec<T>, name: String): Endec<T> {
        return ValueEndecRegistry.register(
            endec,
            KasiMa.id(name)
        )
    }

    fun initialize() {}
}