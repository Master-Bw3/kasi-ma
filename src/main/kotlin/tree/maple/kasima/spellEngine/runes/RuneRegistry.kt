package tree.maple.kasima.spellEngine.runes

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

object RuneRegistry {

    val REGISTRY_KEY: RegistryKey<Registry<Rune>> =
        RegistryKey.ofRegistry(KasiMa.id("rune"))

    private val BLOCK_STATE_ID_LOOKUP: Int2ObjectMap<Identifier> = Int2ObjectOpenHashMap()

    val REGISTRY: Registry<Rune> = FabricRegistryBuilder.from(object :
        SimpleRegistry<Rune>(
            REGISTRY_KEY,
            Lifecycle.stable()
        ) {
        override fun add(
            key: RegistryKey<Rune>,
            value: Rune,
            info: RegistryEntryInfo
        ): RegistryEntry.Reference<Rune> {
            val id = value.blockStateID.toInt()
            if (BLOCK_STATE_ID_LOOKUP.containsKey(id)) {
                KasiMa.logger.warn(
                    "WARNING: collision between two rune ids: ({} overrode {})",
                    key.value, BLOCK_STATE_ID_LOOKUP.get(id)
                )
            }

            BLOCK_STATE_ID_LOOKUP.put(id, key.value)
            return super.add(key, value, info)
        }
    }).buildAndRegister()

    val ADD = register(RuneAdd, "add")
    val ONE = register(RuneOne, "const/one")

    private fun register(type: Rune, name: String): Rune {
        return Registry.register(
            REGISTRY,
            KasiMa.id(name),
            type
        )
    }

    fun getFromBlockStateID(intId: Int): Rune? {
        val id: Identifier =BLOCK_STATE_ID_LOOKUP.get(intId)
            ?: throw IllegalArgumentException("Not a valid int id for fragment type")

        return REGISTRY.get(id)
    }

    fun initialize() {}
}