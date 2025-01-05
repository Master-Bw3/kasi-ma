package tree.maple.kasima.spellEngine.argumentOperators

object ArgOpIdentity : ArgumentOperator() {
    override val stackSignature: Pair<Int, Int> = 1 to 1

    override val functionArgCount: Int = 0

    override fun invoke(stack: List<Int>, functionStack: List<ArgumentOperator>) = stack to functionStack
}