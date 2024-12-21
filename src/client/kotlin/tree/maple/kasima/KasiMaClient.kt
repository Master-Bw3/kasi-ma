package tree.maple.kasima

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.minecraft.client.render.RenderLayer
import net.minecraft.registry.Registries
import tree.maple.kasima.blocks.KasimaBlocks
import tree.maple.kasima.blocks.Translucent

object KasiMaClient : ClientModInitializer {
    override fun onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.

        //TODO: use less gross workaround
        Registries.BLOCK.filter { it is Translucent }.forEach {
            BlockRenderLayerMap.INSTANCE.putBlock(
                it,
                RenderLayer.getTranslucent(),
            )
        }

        BlockRenderLayerMap.INSTANCE.putBlock(
            KasimaBlocks.PALE_RUNE_CORE,
            RenderLayer.getTranslucent(),
        )
    }
}
