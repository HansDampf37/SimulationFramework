package algebra

import format

abstract class IVec: Iterable<Double> {
    abstract operator fun get(i: Int): Double
    abstract operator fun set(i: Int, value: Double)

    abstract val height: Int

    override fun toString(): String {
        return joinToString(
            prefix = "(",
            postfix = ")",
            separator = ", "
        ) { it.format(digitsAfterComma = 2).removeSuffix(".00") }
    }
}
