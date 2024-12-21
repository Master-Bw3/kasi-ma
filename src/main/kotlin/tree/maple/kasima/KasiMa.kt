package tree.maple.kasima

import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory
import tree.maple.kasima.api.registry.RuneRegistry
import tree.maple.kasima.api.registry.ValueEndecRegistry
import tree.maple.kasima.blocks.KasimaBlocks
import tree.maple.kasima.blocks.blockEntities.KasimaBlockEntities
import tree.maple.kasima.items.KasimaItems
import tree.maple.kasima.spellEngine.KasimaRunes
import tree.maple.kasima.spellEngine.types.KasimaValueEndecs


object KasiMa : ModInitializer {

    val id = "kasi-ma"

    fun id(path: String) = Identifier.of(id, path)

    val logger = LoggerFactory.getLogger(id)

    override fun onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        logger.info("Hello Fabric world!")

        ValueEndecRegistry.initialize()
        RuneRegistry.initialize()

        KasimaItems.initialize()
        KasimaBlocks.initialize()
        KasimaRunes.initialize()
        KasimaBlockEntities.initialize()
        KasimaValueEndecs.initialize()
    }
}
