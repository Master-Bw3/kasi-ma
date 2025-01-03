package tree.maple.kasima.items

import net.minecraft.item.Item
import net.minecraft.item.ItemUsageContext
import net.minecraft.util.ActionResult
import tree.maple.kasima.api.registry.RuneBlockTokenRegistry

class Chisel(settings: Settings?) : Item(settings) {

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val blockState = context.world.getBlockState(context.blockPos)
        val block = blockState.block
        val runeEntry = RuneBlockTokenRegistry.firstOrNull { it.block.get() == block }

        if (runeEntry != null) {
            val runeEntries = RuneBlockTokenRegistry.filter { it.material.get() == runeEntry.material.get() }

            val index = runeEntries.indexOfFirst { it.block.get() == block }

            val newRuneBlock = runeEntries.getOrElse(index + 1) { runeEntries.first() }
                .block.get().getStateWithProperties(blockState)

            context.world.setBlockState(context.blockPos, newRuneBlock)

            return ActionResult.SUCCESS
        }

        return ActionResult.PASS
    }
}