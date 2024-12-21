package tree.maple.kasima.spellEngine.runes

import com.mojang.serialization.Lifecycle
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.minecraft.block.Blocks
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.SimpleRegistry
import net.minecraft.util.Identifier
import tree.maple.kasima.KasiMa
import tree.maple.kasima.api.RuneRegistrationHelper
import tree.maple.kasima.api.registry.RuneRegistry
import tree.maple.kasima.spellEngine.runes.Rune
import tree.maple.kasima.spellEngine.runes.RuneAdd
import tree.maple.kasima.spellEngine.runes.RuneOne

object KasimaRunes {

    val PALE_OAK_LOG_ID = Registries.BLOCK.getId(Blocks.PALE_OAK_LOG)

    val ADD = register(RuneAdd, "add", PALE_OAK_LOG_ID)
    val ONE = register(RuneOne, "const/one", PALE_OAK_LOG_ID)
    val GAP = RuneRegistry.register(Gap, { Blocks.PALE_OAK_LOG }, { Blocks.PALE_OAK_LOG }, KasiMa.id("gap"))

    private fun register(rune: Rune, name: String, backingBlock: Identifier): Rune {
        return RuneRegistrationHelper.registerRune(
            rune,
            KasiMa.id(name),
            backingBlock
        ).first
    }

    fun initialize() {}
}