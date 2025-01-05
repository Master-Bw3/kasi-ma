package tree.maple.kasima.spellEngine.argumentOperators

import java.util.LinkedList

abstract class ArgumentOperator {
    abstract val stackSignature: Pair<Int, Int>
    abstract val functionArgCount: Int

    abstract operator fun invoke(stack: List<Int>, functionStack: List<ArgumentOperator>): Pair<List<Int>, List<ArgumentOperator>>

}