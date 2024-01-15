package algebra

import kotlin.math.sqrt

class Vec4(x: Double, y: Double, z: Double, var w: Double) : Vec(x, y, z) {
    override fun toString(): String {
        return "($x, $y, $z, $w)"
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Vec4) {
            (this - other).length() < 0.0001
        } else {
            false
        }
    }

    fun length() = sqrt(x * x + y * y + z * z + w * w)

    operator fun minus(other: Vec4) = Vec4(x - other.x, y - other.y, z - other.z, w - other.w)

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + w.hashCode()
        return result
    }
}

class Vec2(var x: Double, var y: Double) {
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
}