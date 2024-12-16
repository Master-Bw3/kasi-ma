package tree.maple.kasima.spellEngine.types

import com.mojang.serialization.Lifecycle
import io.wispforest.endec.StructEndec
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
import java.util.*

object TypeRegistry {

    val REGISTRY_KEY: RegistryKey<Registry<Type<*>>> =
        RegistryKey.ofRegistry(KasiMa.id("type"))
    val INT_ID_LOOKUP: Int2ObjectMap<Identifier> = Int2ObjectOpenHashMap()
    val REGISTRY: Registry<Type<*>> = FabricRegistryBuilder.from(object :
        SimpleRegistry<Type<*>>(
            REGISTRY_KEY,
            Lifecycle.stable()
        ) {
        override fun add(
            key: RegistryKey<Type<*>>,
            value: Type<*>,
            info: RegistryEntryInfo
        ): RegistryEntry.Reference<Type<*>> {
            val hash = key.value.hashCode()
            if (INT_ID_LOOKUP.containsKey(hash)) {
                KasiMa.logger.warn(
                    "WARNING: Hashcode collision between two types ({} overrode {})",
                    key.value, INT_ID_LOOKUP.get(hash)
                )
            }

            INT_ID_LOOKUP.put(hash, key.value)
            return super.add(key, value, info)
        }
    }).buildAndRegister()


    val NUMBER: Type<NumberValue> = register(NumberValue.TYPE, "number")

    private fun <T : Value> register(type: Type<T>, name: String): Type<T> {
        return Registry.register(
            REGISTRY,
            KasiMa.id(name),
            type
        )
    }

    fun initialize() {}

}