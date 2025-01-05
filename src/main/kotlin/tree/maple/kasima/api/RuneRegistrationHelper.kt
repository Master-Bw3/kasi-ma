package tree.maple.kasima.api

import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.Identifier
import tree.maple.kasima.api.registry.OperatorRegistry
import tree.maple.kasima.blocks.KasimaBlocks
import tree.maple.kasima.blocks.RuneLog
import tree.maple.kasima.api.registry.BlockTokenRegistry
import tree.maple.kasima.spellEngine.compiler.Token
import tree.maple.kasima.spellEngine.operators.Operator

object RuneRegistrationHelper {

    fun registerOperator(
        id: Identifier,
        operator: Operator,
        material: Identifier,
        verticalModel: Identifier,
        horizontalModel: Identifier
    ) = registerToken(id, Token.Operator(id), operator, material, verticalModel, horizontalModel)

    fun registerToken(
        id: Identifier,
        token: Token,
        operator: Operator?,
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
        BlockTokenRegistry.register(
            token,
            material,
            id,
        )

        operator?.let {
            OperatorRegistry.register(
                it,
                id
            )
        }

        return Pair(Token.Operator(id), block)
    }
}