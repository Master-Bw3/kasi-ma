package tree.maple.kasima.api

import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.registry.Registries
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.Identifier
import tree.maple.kasima.blocks.KasimaBlocks
import tree.maple.kasima.blocks.RuneLog
import tree.maple.kasima.spellEngine.Rune
import tree.maple.kasima.api.registry.RuneRegistry

object RuneRegistrationHelper {

    fun registerRune(rune: Rune, id: Identifier, material: Identifier): Pair<Rune, Block> {


        //register block
        val block = KasimaBlocks.register(
            { settings -> RuneLog(settings) },
            Blocks.createLogSettings(
                Blocks.PALE_OAK_PLANKS.defaultMapColor,
                Blocks.PALE_OAK_WOOD.defaultMapColor,
                BlockSoundGroup.WOOD
            ),
            id,
            registerItem = false,
            flammable = true,
        )

        //register rune
        RuneRegistry.register(
            rune,
            { block },
            { Registries.BLOCK.get(material) },
            id,
        )

        return Pair(rune, block)
    }
}