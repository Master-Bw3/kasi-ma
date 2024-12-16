package tree.maple.kasima

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.minecraft.client.render.RenderLayer
import tree.maple.kasima.blocks.KasimaBlocks

object KasiMaClient : ClientModInitializer {
	override fun onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

		BlockRenderLayerMap.INSTANCE.putBlock(KasimaBlocks.OAK_RUNE_LOG, RenderLayer.getTranslucent())
	}
}