package tree.maple.kasima.spellEngine.argumentOperators

object ArgOpFlip : ArgumentOperator() {
    override val stackSignature: Pair<Int, Int> = 2 to 2
    override val functionArgCount: Int = 0

    override fun invoke(stack: List<Int>, functionStack: List<ArgumentOperator>): Pair<List<Int>, List<ArgumentOperator>> {
        return stack.takeLast(2).reversed() + stack.dropLast(2) to functionStack
    }
}