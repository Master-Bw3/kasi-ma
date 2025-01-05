package tree.maple.kasima.spellEngine.argumentOperators

import java.util.*

object ArgOpDip : ArgumentOperator() {
    override val stackSignature: Pair<Int, Int> = 1 to 1
    override val functionArgCount: Int = 1

    override fun invoke(stack: List<Int>, functionStack: List<ArgumentOperator>): Pair<List<Int>, List<ArgumentOperator>> {
        val top = stack.last()
        val (stackResult, functionStackResult) = functionStack.first().invoke(stack.dropLast(1), functionStack.dropLast(1))
        return stackResult + top to functionStackResult
    }
}
