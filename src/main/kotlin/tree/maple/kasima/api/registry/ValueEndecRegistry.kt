package tree.maple.kasima.api.registry

import com.mojang.serialization.Lifecycle
import io.wispforest.endec.Endec
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
import tree.maple.kasima.spellEngine.types.Type
import tree.maple.kasima.spellEngine.types.Value

private val REGISTRY_KEY: RegistryKey<Registry<Endec<out Value>>> =
    RegistryKey.ofRegistry(KasiMa.id("type"))

private val INT_ID_LOOKUP: Int2ObjectMap<Identifier> = Int2ObjectOpenHashMap()
private val REGISTRY: Registry<Endec<out Value>> = FabricRegistryBuilder.from(object :
    SimpleRegistry<Endec<out Value>>(
        REGISTRY_KEY,
        Lifecycle.stable()
    ) {
    override fun add(
        key: RegistryKey<Endec<out Value>>,
        value: Endec<out Value>,
        info: RegistryEntryInfo
    ): RegistryEntry.Reference<Endec<out Value>> {
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

object ValueEndecRegistry : Registry<Endec<out Value>> by REGISTRY {

    fun <T : Value> register(endec: Endec<T>, id: Identifier): Endec<T> {
        return Registry.register(
            REGISTRY,
            id,
            endec
        )
    }

    fun initialize() {}

}