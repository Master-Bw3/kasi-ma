package tree.maple.kasima.spellEngine

import tree.maple.kasima.spellEngine.types.SpellFunction

//basically a token I guess
sealed class Rune {
    data class Function(val function: SpellFunction) : Rune()

    data object Gap : Rune()

    data object Apply : Rune()

}