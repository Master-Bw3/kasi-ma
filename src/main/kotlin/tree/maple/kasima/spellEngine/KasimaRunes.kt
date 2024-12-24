package tree.maple.kasima.spellEngine

import net.minecraft.block.Blocks
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import tree.maple.kasima.KasiMa
import tree.maple.kasima.api.RuneRegistrationHelper
import tree.maple.kasima.api.registry.RuneRegistry
import tree.maple.kasima.spellEngine.operators.OpAdd
import tree.maple.kasima.spellEngine.operators.OpOne
import tree.maple.kasima.spellEngine.types.MaybeType

object KasimaRunes {

    private val PALE_OAK_LOG_ID = Registries.BLOCK.getId(Blocks.PALE_OAK_LOG)

    val ADD = register(Rune.Function(OpAdd), "add", PALE_OAK_LOG_ID)
    val ONE = register(Rune.Function(OpOne), "const/one", PALE_OAK_LOG_ID)
    val SOME = register(Rune.Function(MaybeType.generateConstructor(MaybeType.SOME)), "some", PALE_OAK_LOG_ID)

    val APPLY = register(Rune.Apply, "apply", PALE_OAK_LOG_ID)
    val GAP = RuneRegistry.register(Rune.Gap, { Blocks.PALE_OAK_LOG }, { Blocks.PALE_OAK_LOG }, KasiMa.id("gap"))

    private fun register(rune: Rune, name: String, backingBlock: Identifier): Rune {
        return RuneRegistrationHelper.registerRune(
            rune,
            KasiMa.id(name),
            backingBlock,
            Identifier.ofVanilla("block/pale_oak_log"),
            Identifier.ofVanilla ("block/pale_oak_log_horizontal")
        ).first
    }

    fun initialize() {}
}