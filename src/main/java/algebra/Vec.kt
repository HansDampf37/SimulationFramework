package algebra

import kotlin.math.sqrt

/**
 * Represents a Vector in 3d space.
 * @param x x value
 * @param y y value
 * @param z z value
 */
open class Vec(var x: Double, var y: Double, var z: Double) {

    constructor(x: Number, y: Number, z: Number) : this(x.toDouble(), y.toDouble(), z.toDouble())
    /**
     * Returns the result of the vector addition v + w. The returned vector is safe to use.
     *
     * @param w other vector
     * @return v + w
     */
    operator fun plus(w: Vec): Vec {
        return Vec(x + w.x, y + w.y, z + w.z)
    }

    /**
     * Returns the result of the vector subtraction v - w. The returned vector is safe to use.
     *
     * @param w other vector
     * @return v - w
     */
    operator fun minus(w: Vec): Vec {
        return Vec(x - w.x, y - w.y, z - w.z)
    }

    /**
     * Returns the result of the scalar multiplication v * scalar without changing v. The returned vector is safe to use.
     * @param scalar factor
     * @return the result of the scalar multiplication v * scalar
     */
    operator fun times(scalar: Double): Vec {
        return Vec(x * scalar, y * scalar, z * scalar)
    }

    operator fun times(other: Vec) = dotProduct(other)
    operator fun div(scalar: Number) = this * (1.0 / scalar.toDouble())

    operator fun unaryMinus(): Vec = Vec(-x, -y, -z)

    /**
     * Returns a vector with the same direction as the given one, but length 1. The returned vector is safe to use.
     *
     * @return a vector with the same direction as the given one, but length 1.
     */
    fun normalize(): Vec {
        if (length == 0.0) throw ArithmeticException("Division by zero")
        return Vec(x / length, y / length, z / length)
    }


    val length: Double
        /**
         * Returns the length of this vector
         *
         * @return length
         */
        get() = sqrt(x * x + y * y + z * z)

    /**
     * Returns the standard dot product (fundamental matrix = unit matrix)with the given vector.
     */
    fun dotProduct(v: Vec): Double {
        return x * v.x + y * v.y + z * v.z
    }

    /**
     * Returns true if the angle between this vector and the given one is smaller than 90°. Otherwise, returns false.
     */
    fun hasSharpAngleTo(v: Vec): Boolean {
        val w = projectOnto(v)
        return w.x * v.x > 0 || w.y * v.y > 0 || w.z * v.z > 0
    }


    /**
     * Returns the part of this vector that is parallel to the given vector.
     * The returned vector is safe to use.
     *
     * @param ontoThisOne the returned vector is parallel to this one
     * @return the part of this vector that is parallel to the given vector
     */
    fun projectOnto(ontoThisOne: Vec): Vec {
        var w = Vec(ontoThisOne.x, ontoThisOne.y, ontoThisOne.z)
        w *= (this * (w) / (w * w))
        return w
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Vec) {
            (this - other).length < 0.0001
        } else false
    }

    override fun toString(): String {
        return "($x, $y, $z)"
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        return result
    }

    fun setToZero() {
        x = 0.0
        y = 0.0
        z = 0.0
    }

    /**
     * adds the given Vector to this Vector and returns this instance with updated values
     *
     * @param vec other vector
     * @return the result o the vector addition with this vector and v
     */
    fun addInPlace(vec: Vec): Vec {
        x += vec.x
        y += vec.y
        z += vec.z
        return this
    }

    fun scaleInPlace(scalar: Double): Vec {
        x *= scalar
        y *= scalar
        z *= scalar
        return this
    }

    /**
     * subtracts the given Vector from this Vector and returns this instance with updated values
     *
     * @param vec other vector
     * @return the result o the vector subtraction with this vector and v
     */
    fun subInPlace(vec: Vec): Vec {
        x -= vec.x
        y -= vec.y
        z -= vec.z
        return this
    }

    fun crossProduct(vec: Vec): Vec {
        return Vec(y * vec.z - z * vec.y, z * vec.x - x * vec.z, x * vec.y - y * vec.x)
    }
}