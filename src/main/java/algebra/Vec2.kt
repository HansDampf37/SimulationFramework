package algebra

import kotlin.math.sqrt

class Vec2(var x: Double, var y: Double) {

    constructor(x: Number, y: Number) : this(x.toDouble(), y.toDouble())
    override fun toString(): String {
        return "($x, $y)"
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Vec2) {
            (this - other).length() < 0.0001
        } else {
            false
        }
    }

    fun length() = sqrt(x * x + y * y)

    operator fun minus(other: Vec2) = Vec2(x - other.x, y - other.y)
    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }

    fun normalize(): Vec2 {
        return this * (1 / length())
    }

    operator fun times(d: Double): Vec2 {
        return Vec2(x * d, y * d)
    }

    operator fun times(v: Vec2): Double {
        return this.x * v.x + this.y * v.y
    }
}