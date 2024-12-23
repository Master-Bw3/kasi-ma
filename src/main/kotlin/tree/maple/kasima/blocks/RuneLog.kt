package tree.maple.kasima.blocks

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.PillarBlock
import net.minecraft.state.StateManager
import net.minecraft.state.property.IntProperty
import net.minecraft.util.Identifier


class RuneLog(settings: Settings, val verticalModel: Identifier, val horizontalModel: Identifier, val runeIdentifier: Identifier)
    : PillarBlock(settings), Translucent, AxeMineable {

    init {
        defaultState = defaultState.with(RUNE, 0)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState?>) {
        builder.add(RUNE)
        super.appendProperties(builder)
    }


    companion object {
        val RUNE: IntProperty = IntProperty.of("rune", 0, 500)
    }
}