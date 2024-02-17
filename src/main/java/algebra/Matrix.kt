package algebra

import digitsAfterComma
import digitsBeforeComma
import format
import kotlin.math.abs
import kotlin.math.min

class Matrix(private val rows: Array<Vec>) : Iterable<Double> {
    init {
        if (rows.isEmpty() || rows.any { it.isEmpty() }) throw IllegalArgumentException("Empty rows/columns are not allowed")
        if (rows.any { it.height != rows[0].height }) throw IllegalArgumentException("Rows are not equally long")
    }

    operator fun get(i: Int) = rows[i]
    fun column(j: Int): Vec = Vec(Array(height) { i -> rows[i][j] })
    val height = rows.size
    val width = rows[0].height
    operator fun times(v: Vec): Vec {
        if (v.height != this.width) throw DimensionException("Can not multiply $this with $v")
        return Vec(Array(height) { i -> rows[i] * v })
    }

    operator fun times(other: Matrix): Matrix {
        if (other.height != this.width) throw DimensionException("Can not multiply $this with $other")
        return Matrix(Array(this.height) { i ->
            Vec(Array(other.width) { j ->
                rows[i] * other.column(j)
            })
        })
    }

    operator fun times(scalar: Number): Matrix {
        return Matrix(Array(height) { i -> rows[i] * scalar })
    }

    operator fun plus(other: Matrix): Matrix {
        return Matrix(height, width) { i, j -> rows[i][j] + other.rows[i][j] }
    }

    private operator fun minus(other: Matrix): Matrix {
        return Matrix(height, width) { i, j -> rows[i][j] - other.rows[i][j] }
    }

    operator fun plus(scalar: Number): Matrix {
        return Matrix(height, width) { i, j -> rows[i][j] + scalar.toDouble() }
    }

    operator fun minus(scalar: Number): Matrix {
        return Matrix(height, width) { i, j -> rows[i][j] - scalar.toDouble() }
    }

    fun transpose() = Matrix(width, height) { i, j -> rows[j][i] }
    val size: Int
        get() = height * width

    override fun equals(other: Any?): Boolean {
        if (other !is Matrix) return false
        if (other.height != this.height || other.width != this.width) return false
        return (this - other).all { abs(it) < 0.00001 }
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

    private fun all(operation: (Double) -> Boolean): Boolean {
        val iterator = iterator()
        while (iterator.hasNext()) {
            if (!operation(iterator.next())) return false
        }
        return true
    }

    override fun toString(): String {
        val maxDigitsBeforeComma = Array(width) { j -> column(j).maxOf { entry -> entry.digitsBeforeComma() } }
        val maxDigitsAfterComma = Array(width) { j -> column(j).maxOf { entry -> entry.digitsAfterComma() } }
        return rows.joinToString(prefix = "|", postfix = "|", separator = "|\n|") { row ->
            var i: Int = 0
            row.joinToString(separator = ", ") {
                it.format(
                    digitsBeforeComma = maxDigitsBeforeComma[i],
                    digitsAfterComma = min(3, maxDigitsAfterComma[i++])
                )
            }
        }
    }

    constructor(
        height: Int,
        width: Int,
        vararg entries: Number
    ) : this(Array(height) { i -> Vec(Array(width) { j -> entries[i * width + j].toDouble() }) })

    constructor(height: Int, width: Int, operation: (i: Int, j: Int) -> Number) :
            this(Array(height) { i -> Vec(Array(width) { j -> operation(i, j).toDouble() }) })

    companion object {
        fun eye(height: Int, width: Int) = Matrix(height, width) { i, j -> if (i == j) 1 else 0 }

        fun zero(height: Int, width: Int): Matrix = Matrix(height, width) { _, _ -> 0 }
    }
}