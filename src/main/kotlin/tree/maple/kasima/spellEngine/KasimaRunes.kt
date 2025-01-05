package tree.maple.kasima.spellEngine

import net.minecraft.block.Blocks
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import tree.maple.kasima.KasiMa
import tree.maple.kasima.api.RuneRegistrationHelper
import tree.maple.kasima.api.registry.BlockTokenRegistry
import tree.maple.kasima.api.registry.OperatorRegistry
import tree.maple.kasima.spellEngine.compiler.Token
import tree.maple.kasima.spellEngine.operators.*

object KasimaRunes {

    private val PALE_OAK_LOG_ID = Registries.BLOCK.getId(Blocks.PALE_OAK_LOG)

    val ADD = RuneRegistrationHelper.registerOperator(
        KasiMa.id("add"),
        OpAdd,
        PALE_OAK_LOG_ID,
        Identifier.ofVanilla("block/pale_oak_log"),
        Identifier.ofVanilla("block/pale_oak_log_horizontal")
    )

    val ONE = RuneRegistrationHelper.registerOperator(
        KasiMa.id("const/one"),
        OpOne,
        PALE_OAK_LOG_ID,
        Identifier.ofVanilla("block/pale_oak_log"),
        Identifier.ofVanilla("block/pale_oak_log_horizontal")
    )

    val LIST_SINGLETON = RuneRegistrationHelper.registerOperator(
        KasiMa.id("list/singleton"),
        OpSingleton,
        PALE_OAK_LOG_ID,
        Identifier.ofVanilla("block/pale_oak_log"),
        Identifier.ofVanilla("block/pale_oak_log_horizontal")
    )

    val LIST_PLUS = RuneRegistrationHelper.registerOperator(
        KasiMa.id("list/plus"),
        OpListPlus,
        PALE_OAK_LOG_ID,
        Identifier.ofVanilla("block/pale_oak_log"),
        Identifier.ofVanilla("block/pale_oak_log_horizontal")
    )

    val LIST_MAP = RuneRegistrationHelper.registerOperator(
        KasiMa.id("list/map"),
        OpListMap,
        PALE_OAK_LOG_ID,
        Identifier.ofVanilla("block/pale_oak_log"),
        Identifier.ofVanilla("block/pale_oak_log_horizontal")
    )


    val GAP = BlockTokenRegistry.register(
        Token.Gap,
        PALE_OAK_LOG_ID,
        PALE_OAK_LOG_ID
    )

    val COMPOSE = RuneRegistrationHelper.registerToken(
        KasiMa.id("compose"),
        Token.Compose,
        null,
        PALE_OAK_LOG_ID,
        Identifier.ofVanilla("block/pale_oak_log"),
        Identifier.ofVanilla("block/pale_oak_log_horizontal")
    ).also { OperatorRegistry.register(OpCompose, KasiMa.id("compose")) }



    fun initialize() {}
}