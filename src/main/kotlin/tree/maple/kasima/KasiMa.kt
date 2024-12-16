package tree.maple.kasima

import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory
import tree.maple.kasima.blocks.KasimaBlockRegistry
import tree.maple.kasima.spellEngine.ASTNode
import tree.maple.kasima.spellEngine.compile
import tree.maple.kasima.spellEngine.runes.RuneAdd
import tree.maple.kasima.spellEngine.runes.RuneOne
import tree.maple.kasima.spellEngine.typeCheck
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

        KasimaBlockRegistry.initialize()
        TypeRegistry.initialize()


        val ast = ASTNode(
            RuneAdd, listOf(
            ASTNode(RuneOne, listOf()),
            ASTNode(
                RuneAdd, listOf(
                ASTNode(RuneOne, listOf()),
                ASTNode(RuneOne, listOf())
            ))
        ))

        println(typeCheck(ast))



    }
}