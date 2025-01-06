package tree.maple.kasima.spellEngine

import net.minecraft.block.Blocks
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import tree.maple.kasima.KasiMa
import tree.maple.kasima.api.RuneRegistrationHelper
import tree.maple.kasima.api.registry.BlockTokenRegistry
import tree.maple.kasima.api.registry.OperatorRegistry
import tree.maple.kasima.spellEngine.compiler.BuiltInType
import tree.maple.kasima.spellEngine.compiler.Token
import tree.maple.kasima.spellEngine.operators.*
import tree.maple.kasima.spellEngine.operators.composition.*

object KasimaRunes {

    private val PALE_OAK_LOG_ID = Registries.BLOCK.getId(Blocks.PALE_OAK_LOG)

    val ADD = RuneRegistrationHelper.registerOperator(
        KasiMa.id("add"),
        OpAdd,
        PALE_OAK_LOG_ID,
        Identifier.ofVanilla("block/pale_oak_log"),
        Identifier.ofVanilla("block/pale_oak_log_horizontal")
    )

    val ONE = RuneRegistrationHelper.registerToken(
        KasiMa.id("const/one"),
        Token.Constant(1, BuiltInType.number),
        null,
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

    val INFIX_COMPOSE = RuneRegistrationHelper.registerToken(
        KasiMa.id("infix_compose"),
        Token.Compose,
        null,
        PALE_OAK_LOG_ID,
        Identifier.ofVanilla("block/pale_oak_log"),
        Identifier.ofVanilla("block/pale_oak_log_horizontal")
    )

    val COMPOSE = RuneRegistrationHelper.registerOperator(
        KasiMa.id("compose"),
        OpCompose,
        PALE_OAK_LOG_ID,
        Identifier.ofVanilla("block/pale_oak_log"),
        Identifier.ofVanilla("block/pale_oak_log_horizontal")
    )

    val CURRY = RuneRegistrationHelper.registerOperator(
        KasiMa.id("curry"),
        OpCurry,
        PALE_OAK_LOG_ID,
        Identifier.ofVanilla("block/pale_oak_log"),
        Identifier.ofVanilla("block/pale_oak_log_horizontal")
    )

    val UNCURRY = RuneRegistrationHelper.registerOperator(
        KasiMa.id("uncurry"),
        OpUncurry,
        PALE_OAK_LOG_ID,
        Identifier.ofVanilla("block/pale_oak_log"),
        Identifier.ofVanilla("block/pale_oak_log_horizontal")
    )

    val DIP = RuneRegistrationHelper.registerOperator(
        KasiMa.id("dip"),
        OpDip,
        PALE_OAK_LOG_ID,
        Identifier.ofVanilla("block/pale_oak_log"),
        Identifier.ofVanilla("block/pale_oak_log_horizontal")
    )

    val RIGHT = RuneRegistrationHelper.registerOperator(
        KasiMa.id("right"),
        OpRight,
        PALE_OAK_LOG_ID,
        Identifier.ofVanilla("block/pale_oak_log"),
        Identifier.ofVanilla("block/pale_oak_log_horizontal")
    )

    val FLIP = RuneRegistrationHelper.registerOperator(
        KasiMa.id("flip"),
        OpFlip,
        PALE_OAK_LOG_ID,
        Identifier.ofVanilla("block/pale_oak_log"),
        Identifier.ofVanilla("block/pale_oak_log_horizontal")
    )

    val FORK = RuneRegistrationHelper.registerOperator(
        KasiMa.id("fork"),
        OpFork,
        PALE_OAK_LOG_ID,
        Identifier.ofVanilla("block/pale_oak_log"),
        Identifier.ofVanilla("block/pale_oak_log_horizontal")
    )


    fun initialize() {}
}