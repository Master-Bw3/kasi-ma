package tree.maple.kasima.spellEngine.types

import com.mojang.serialization.Lifecycle
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.SimpleRegistry
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.registry.entry.RegistryEntryInfo
import net.minecraft.util.Identifier
import tree.maple.kasima.KasiMa
import tree.maple.kasima.api.registry.TypeRegistry
import tree.maple.kasima.spellEngine.types.BooleanValue
import tree.maple.kasima.spellEngine.types.NumberValue
import tree.maple.kasima.spellEngine.types.Type
import tree.maple.kasima.spellEngine.types.Value

object KasimaTypes {

    val NUMBER: Type<NumberValue> = register(NumberValue.TYPE, "number")
    val BOOLEAN: Type<BooleanValue> = register(BooleanValue.TYPE, "boolean")


    private fun <T : Value> register(type: Type<T>, name: String): Type<T> {
        return TypeRegistry.register(
            type,
            KasiMa.id(name)
        )
    }

    fun initialize() {}
}