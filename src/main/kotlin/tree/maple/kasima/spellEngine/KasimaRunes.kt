package tree.maple.kasima.spellEngine

import net.minecraft.block.Blocks
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import tree.maple.kasima.KasiMa
import tree.maple.kasima.api.RuneRegistrationHelper
import tree.maple.kasima.api.registry.RuneBlockTokenRegistry
import tree.maple.kasima.spellEngine.compiler.Token
import tree.maple.kasima.spellEngine.operators.*

object KasimaRunes {

    private val PALE_OAK_LOG_ID = Registries.BLOCK.getId(Blocks.PALE_OAK_LOG)

    val ADD = register(OpAdd, "add", PALE_OAK_LOG_ID)
    val ONE = register(OpOne, "const/one", PALE_OAK_LOG_ID)

    val LIST_SINGLETON = register(OpSingleton, "list/singleton", PALE_OAK_LOG_ID)
    val LIST_PLUS = register(OpListPlus, "list/plus", PALE_OAK_LOG_ID)
    val LIST_MAP = register(OpListMap, "list/map", PALE_OAK_LOG_ID)


    val GAP = RuneBlockTokenRegistry.register(Token.Gap, null, { Blocks.PALE_OAK_LOG }, { Blocks.PALE_OAK_LOG }, KasiMa.id("gap"))

    private fun register(function: Operator, name: String, backingBlock: Identifier): Token {
        return RuneRegistrationHelper.registerRune(
            KasiMa.id(name),
            function,
            backingBlock,
            Identifier.ofVanilla("block/pale_oak_log"),
            Identifier.ofVanilla ("block/pale_oak_log_horizontal")
        ).first
    }

    fun initialize() {}
}