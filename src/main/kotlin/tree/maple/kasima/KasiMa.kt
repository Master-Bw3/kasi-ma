package tree.maple.kasima

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory
import tree.maple.kasima.blocks.KasimaBlocks
import tree.maple.kasima.spellEngine.ASTNode
import tree.maple.kasima.spellEngine.compile
import tree.maple.kasima.spellEngine.runes.RuneAdd
import tree.maple.kasima.spellEngine.runes.RuneOne

object KasiMa : ModInitializer {

	val id = "kasi-ma"

    private val logger = LoggerFactory.getLogger(id)

    override fun onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        logger.info("Hello Fabric world!")

        KasimaBlocks.initialize()


        val ast = ASTNode(
            RuneAdd, listOf(
            ASTNode(RuneOne, listOf()),
            ASTNode(
                RuneAdd, listOf(
                ASTNode(RuneOne, listOf()),
                ASTNode(RuneOne, listOf())
            ))
        ))

        val compiled = compile(ast)


        println(compiled.handle.invoke())
    }
}