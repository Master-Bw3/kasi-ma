package tree.maple.kasima.items

import net.minecraft.item.Item
import net.minecraft.item.ItemUsageContext
import net.minecraft.registry.Registries
import net.minecraft.state.property.Property
import net.minecraft.util.ActionResult
import net.minecraft.util.math.MathHelper
import tree.maple.kasima.blocks.KasimaBlockTags
import tree.maple.kasima.blocks.KasimaChiselConversionRegistry
import tree.maple.kasima.blocks.RuneLog

class Chisel(settings: Settings?) : Item(settings) {

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val blockState = context.world.getBlockState(context.blockPos)
        val blockID = Registries.BLOCK.getId(blockState.block)
        val convertsTo = KasimaChiselConversionRegistry.REGISTRY.get(blockID)

        if (convertsTo != null) {
            context.world.setBlockState(context.blockPos, convertsTo.getStateWithProperties(blockState))

            return ActionResult.SUCCESS

        } else {
            val rune = blockState.getNullable(RuneLog.RUNE)
            if (blockState.isIn(KasimaBlockTags.CHISELABLE) && rune != null) {
                val sneaking = context.player?.isSneaking ?: false

                val newRune = MathHelper.clamp(if (sneaking) rune - 1 else rune + 1, 0, 500)

                context.world.setBlockState(context.blockPos, blockState.with(RuneLog.RUNE, newRune))

                return ActionResult.SUCCESS
            }
        }

        return ActionResult.PASS
    }
}