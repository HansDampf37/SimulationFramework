package algebra

import digitsAfterComma
import digitsBeforeComma
import format

abstract class IMatrix<R : IVec, C : IVec> : Iterable<Double> {
    abstract fun getColumn(j: Int): C
    abstract fun getRow(i: Int): R
    abstract val width: Int
    abstract val height: Int

    open val rows get() = IntRange(0, height).map { getRow(it) }

    abstract operator fun get(i: Int, j: Int): Double
    abstract operator fun set(i: Int, j: Int, value: Double)

    override fun toString(): String {
        val maxDigitsBeforeComma = Array(width) { j -> getColumn(j).maxOf { entry -> entry.digitsBeforeComma() } }
        val maxDigitsAfterComma = Array(width) { j -> getColumn(j).maxOf { entry -> entry.digitsAfterComma() } }
        return rows.joinToString(prefix = "|", postfix = "|", separator = "|\n|") { row ->
                var i: Int = 0
                row.joinToString(separator = ", ") {
                    it.format(
                        digitsBeforeComma = maxDigitsBeforeComma[i],
                        digitsAfterComma = kotlin.math.min(3, maxDigitsAfterComma[i++])
                    )
                }
            }
    }

    override fun iterator(): Iterator<Double> {
        return object : Iterator<Double> {
            var nextI: Int = 0
            var nextJ: Int = 0

            override fun hasNext(): Boolean {
                return nextI < height
            }

            override fun next(): Double {
                return rows[nextI][nextJ].apply {
                    if (nextJ + 1 < width) {
                        nextJ++
                    } else {
                        nextJ = 0
                        nextI++
                    }
                }
            }
        }
    }
}