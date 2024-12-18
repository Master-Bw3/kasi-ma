package tree.maple.kasima

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.minecraft.client.render.RenderLayer
import tree.maple.kasima.blocks.KasimaBlockRegistry

object KasiMaClient : ClientModInitializer {
    override fun onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.

        BlockRenderLayerMap.INSTANCE.putBlocks(
            RenderLayer.getTranslucent(),
            KasimaBlockRegistry.PALE_RUNE_LOG,
            KasimaBlockRegistry.PALE_RUNE_CORE
        )
    }
}