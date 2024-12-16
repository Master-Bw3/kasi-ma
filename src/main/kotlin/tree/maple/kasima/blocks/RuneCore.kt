package tree.maple.kasima.blocks

import com.mojang.serialization.MapCodec
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.state.StateManager
import net.minecraft.state.property.IntProperty
import net.minecraft.util.ActionResult
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.block.WireOrientation
import tree.maple.kasima.blocks.blockEntities.RuneCoreBlockEntity
import javax.swing.event.HyperlinkEvent.EventType.ACTIVATED


class RuneCore(settings: Settings) : BlockWithEntity(settings.nonOpaque()) {

    init {
        defaultState = defaultState.with(RUNE, 0)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(RUNE)
        builder.add(PillarBlock.AXIS)
        super.appendProperties(builder)
    }

    override fun getCodec(): MapCodec<RuneCore>? {
        return createCodec(::RuneCore)
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState) = RuneCoreBlockEntity(pos, state)

    override fun getRenderType(state: BlockState) = BlockRenderType.MODEL

    override fun onUse(
        state: BlockState?,
        world: World?,
        pos: BlockPos?,
        player: PlayerEntity?,
        hit: BlockHitResult?
    ): ActionResult {
        println("hey!")
        return super.onUse(state, world, pos, player, hit)
    }

    override fun neighborUpdate(
        state: BlockState?,
        world: World?,
        pos: BlockPos?,
        sourceBlock: Block?,
        wireOrientation: WireOrientation?,
        notify: Boolean
    ) {
        super.neighborUpdate(state, world, pos, sourceBlock, wireOrientation, notify)
    }

    companion object {
        val RUNE: IntProperty = IntProperty.of("rune", 0, 500)
    }
}