package algebra

import kotlin.math.sqrt

class Vec4(var x: Double, var y: Double, var z: Double, var w: Double) : IVec() {
    override fun equals(other: Any?): Boolean {
        return if (other is Vec4) {
            (this - other).length() < 0.0001
        } else {
            false
        }
    }

    fun length() = sqrt(x * x + y * y + z * z + w * w)

    operator fun minus(other: Vec4) = Vec4(x - other.x, y - other.y, z - other.z, w - other.w)


    override fun iterator(): Iterator<Double> {
        return listOf(x, y, z, w).iterator()
    }

    override fun get(i: Int): Double {
        return when (i) {
            0 -> x
            1 -> y
            2 -> z
            3 -> w
            else -> throw IndexOutOfBoundsException()
        }
    }

    override fun set(i: Int, value: Double) {
        when (i) {
            0 -> x = value
            1 -> y = value
            2 -> z = value
            3 -> w = value
            else -> throw IndexOutOfBoundsException()
        }
    }

    override val height: Int = 4

    constructor(x: Number, y: Number, z: Number, w: Number) : this(x.toDouble(), y.toDouble(), z.toDouble(), w.toDouble())
}

