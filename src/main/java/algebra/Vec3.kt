package algebra

import kotlin.math.acos
import kotlin.math.sqrt

/**
 * Represents a Vector in 3d space.
 * @param x x value
 * @param y y value
 * @param z z value
 */
open class Vec3(var x: Double, var y: Double, var z: Double): IVec() {

    constructor(x: Number, y: Number, z: Number) : this(x.toDouble(), y.toDouble(), z.toDouble())
    /**
     * Returns the result of the vector addition v + w. The returned vector is safe to use.
     *
     * @param w other vector
     * @return v + w
     */
    operator fun plus(w: Vec3): Vec3 {
        return Vec3(x + w.x, y + w.y, z + w.z)
    }

    /**
     * Returns the result of the vector subtraction v - w. The returned vector is safe to use.
     *
     * @param w other vector
     * @return v - w
     */
    operator fun minus(w: Vec3): Vec3 {
        return Vec3(x - w.x, y - w.y, z - w.z)
    }

    /**
     * Returns the result of the subtraction v - s. The returned vector is safe to use.
     *
     * @param s other vector
     * @return v - s
     */
    operator fun plus(s: Number): Vec3 {
        return Vec3(x + s.toFloat(), y + s.toFloat(), z + s.toFloat())
    }

    /**
     * Returns the result of the subtraction v - s. The returned vector is safe to use.
     *
     * @param s other vector
     * @return v - s
     */
    operator fun minus(s: Number): Vec3 {
        return Vec3(x - s.toFloat(), y - s.toFloat(), z - s.toFloat())
    }

    /**
     * Returns the result of the scalar multiplication v * scalar without changing v. The returned vector is safe to use.
     * @param scalar factor
     * @return the result of the scalar multiplication v * scalar
     */
    operator fun times(scalar: Number): Vec3 {
        return Vec3(x * scalar.toDouble(), y * scalar.toDouble(), z * scalar.toDouble())
    }

    operator fun times(other: Vec3) = dotProduct(other)
    operator fun div(scalar: Number) = this * (1.0 / scalar.toDouble())

    operator fun unaryMinus(): Vec3 = Vec3(-x, -y, -z)

    /**
     * Returns a vector with the same direction as the given one, but length 1. The returned vector is safe to use.
     *
     * @return a vector with the same direction as the given one, but length 1.
     */
    fun normalize(): Vec3 {
        if (length == 0.0) throw ArithmeticException("Division by zero")
        return Vec3(x / length, y / length, z / length)
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
    fun dotProduct(v: Vec3): Double {
        return x * v.x + y * v.y + z * v.z
    }

    /**
     * Returns true if the angle between this vector and the given one is smaller than 90°. Otherwise, returns false.
     */
    fun hasSharpAngleTo(v: Vec3): Boolean {
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
    fun projectOnto(ontoThisOne: Vec3): Vec3 {
        var w = Vec3(ontoThisOne.x, ontoThisOne.y, ontoThisOne.z)
        w *= (this * (w) / (w * w))
        return w
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Vec3) {
            x == other.x && y == other.y && z == other.z
        } else false
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        return result
    }

    fun setTo(other: Vec3) {
        this.x = other.x
        this.y = other.y
        this.z = other.z
    }

    /**
     * adds the given Vector to this Vector and returns this instance with updated values
     *
     * @param vec other vector
     * @return the result o the vector addition with this vector and v
     */
    fun addInPlace(vec: Vec3): Vec3 {
        x += vec.x
        y += vec.y
        z += vec.z
        return this
    }

    fun scaleInPlace(scalar: Double): Vec3 {
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
    fun subInPlace(vec: Vec3): Vec3 {
        x -= vec.x
        y -= vec.y
        z -= vec.z
        return this
    }

    fun crossProduct(vec: Vec3): Vec3 {
        return Vec3(y * vec.z - z * vec.y, z * vec.x - x * vec.z, x * vec.y - y * vec.x)
    }

    operator fun times(x: Int): Vec3 {
        return times(x.toDouble())
    }

    fun angleWith(vec: Vec3): Double {
        return acos(vec * this / (vec.length * this.length))
    }

    fun removeComponentParallelTo(direction: Vec3): Vec3 {
        return this - projectOnto(direction)
    }

    override fun iterator(): Iterator<Double> {
        return listOf(x, y, z).iterator()
    }

    override fun get(i: Int): Double {
        return when (i) {
            0 -> x
            1 -> y
            2 -> z
            else -> throw IndexOutOfBoundsException()
        }
    }

    override fun set(i: Int, value: Double) {
        when (i) {
            0 -> x = value
            1 -> y = value
            2 -> z = value
            else -> throw IndexOutOfBoundsException()
        }
    }

    override val height: Int = 3

    companion object{
        val ones = Vec3(1.0, 1.0, 1.0)
        val zero = Vec3(0.0,0.0,0.0)
        val random get() = Vec3(Math.random(), Math.random(), Math.random())
    }
}