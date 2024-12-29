package tree.maple.kasima.api

import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.registry.Registries
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.Identifier
import tree.maple.kasima.blocks.KasimaBlocks
import tree.maple.kasima.blocks.RuneLog
import tree.maple.kasima.api.registry.RuneBlockTokenRegistry
import tree.maple.kasima.spellEngine.compiler.Token
import tree.maple.kasima.spellEngine.operators.Operator

object RuneRegistrationHelper {

    fun registerRune(
        id: Identifier,
        function: Operator,
        material: Identifier,
        verticalModel: Identifier,
        horizontalModel: Identifier
    ): Pair<Token, Block> {
        //register block
        val block = KasimaBlocks.register(
            { settings -> RuneLog(settings, verticalModel, horizontalModel, id) },
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
        RuneBlockTokenRegistry.register(
            Token.Operator(id),
            function,
            { block },
            { Registries.BLOCK.get(material) },
            id,
        )

        return Pair(Token.Operator(id), block)
    }
}