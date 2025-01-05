package tree.maple.kasima.items

import net.minecraft.item.Item
import net.minecraft.item.ItemUsageContext
import net.minecraft.registry.Registries
import net.minecraft.util.ActionResult
import tree.maple.kasima.api.registry.BlockTokenRegistry

class Chisel(settings: Settings?) : Item(settings) {

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val blockState = context.world.getBlockState(context.blockPos)
        val block = blockState.block
        val tokenEntry = BlockTokenRegistry[Registries.BLOCK.getId(block)]

        if (tokenEntry != null) {
            val runeEntries = BlockTokenRegistry.entrySet
                .map { it.key.value to it.value }
                .filter { (_, v) -> v.material == tokenEntry.material }

            val index = runeEntries.indexOfFirst { (k, _) -> k == Registries.BLOCK.getId(block) }

            val newRuneBlock = Registries.BLOCK
                .get(runeEntries.getOrElse(index + 1) { runeEntries.first() }.first)
                .getStateWithProperties(blockState)

            context.world.setBlockState(context.blockPos, newRuneBlock)

            return ActionResult.SUCCESS
        }

        return ActionResult.PASS
    }
}