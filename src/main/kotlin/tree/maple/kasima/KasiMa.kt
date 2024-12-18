package tree.maple.kasima

import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory
import tree.maple.kasima.blocks.KasimaBlockRegistry
import tree.maple.kasima.blocks.KasimaChiselConversionRegistry
import tree.maple.kasima.blocks.blockEntities.KasimaBlockEntityTypeRegistry
import tree.maple.kasima.items.KasimaItemRegistry
import tree.maple.kasima.spellEngine.ASTNode
import tree.maple.kasima.spellEngine.TypeError
import tree.maple.kasima.spellEngine.compile
import tree.maple.kasima.spellEngine.runes.*
import tree.maple.kasima.spellEngine.types.TypeRegistry

object KasiMa : ModInitializer {

    val id = "kasi-ma"

    fun id(path: String) = Identifier.of(id, path)

    val logger = LoggerFactory.getLogger(id)

    override fun onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        logger.info("Hello Fabric world!")

        KasimaItemRegistry.initialize()
        KasimaBlockRegistry.initialize()
        KasimaBlockEntityTypeRegistry.initialize()
        TypeRegistry.initialize()
        RuneRegistry.initialize()
        KasimaChiselConversionRegistry.initialize()





    }
}