package algebra

import format
import java.lang.Math.random
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.sqrt

class Vec(private val entries: Array<Double>): Iterable<Double> {
    init {
        if (entries.isEmpty()) throw IllegalArgumentException("Empty Vectors are not allowed")
    }

    operator fun get(i: Int) = entries[i]
    operator fun set(i: Int, value: Number) {
        entries[i] = value.toDouble()
    }

    /**
     * Returns the result of the vector addition v + w. The returned vector is safe to use.
     *
     * @param other other vector
     * @return v + w
     */
    operator fun plus(other: Vec): Vec {
        return if (other.height == this.height) {
            Vec(Array(height) { i -> entries[i] + other.entries[i] })
        } else {
            throw DimensionException("Can not add $this and $other")
        }
    }

    /**
     * Returns the result of the vector subtraction v - w. The returned vector is safe to use.
     *
     * @param other other vector
     * @return v - w
     */
    operator fun minus(other: Vec): Vec {
        return if (other.height == this.height) {
            Vec(Array(height) { i -> this.entries[i] - other.entries[i] })
        } else {
            throw DimensionException("Cannot subtract $this and $other")
        }
    }

    operator fun unaryMinus(): Vec {
        return Vec(Array(height) { i -> -entries[i] })
    }

    /**
     * Returns the standard dot product (fundamental matrix = unit matrix) with the given vector.
     */
    operator fun times(other: Vec): Double {
        return entries.foldIndexed(0.0) { i, acc, _ -> acc + other.entries[i] * this.entries[i] }
    }

    fun dotProduct(other: Vec): Double = times(other)

    /**
     * Returns the result of the scalar multiplication v * scalar. The returned vector is safe to use.
     * @param scalar factor
     * @return the result of the scalar multiplication v * scalar
     */
    operator fun times(scalar: Number): Vec {
        return Vec(Array(height) { i -> entries[i] * scalar.toDouble() })
    }

    operator fun div(scalar: Number): Vec {
        return Vec(Array(height) { i -> entries[i] / scalar.toDouble() })
    }

    operator fun plus(scalar: Number): Vec {
        return Vec(Array(height) { i -> entries[i] + scalar.toDouble() })
    }

    /**
     * Returns the result of the subtraction v - scalar. The returned vector is safe to use.
     *
     * @param scalar some number
     * @return v - s
     */
    operator fun minus(scalar: Number): Vec {
        return Vec(Array(height) { i -> entries[i] - scalar.toDouble() })
    }

    /**
     * adds the given Vector to this Vector and returns this instance with updated values
     *
     * @param other other vector
     * @return the result of the vector addition with this vector and v
     */
    fun addInPlace(other: Vec): Vec {
        for (i in entries.indices) entries[i] += other.entries[i]
        return this
    }

    /**
     * scales this Vector and returns this instance with updated values
     *
     * @param scalar the scalar
     * @return the result of the scalar multiplication with this vector and the scalar
     */
    fun scaleInPlace(scalar: Double): Vec {
        for (i in entries.indices) entries[i] *= scalar
        return this
    }

    /**
     * Returns a vector with the same direction as the given one, but length 1. The returned vector is safe to use.
     *
     * @return a vector with the same direction as the given one, but length 1.
     */
    fun normalize(): Vec {
        val len = length
        if (len == 0.0) throw ArithmeticException("Division by zero")
        return this / len
    }

    val indices = entries.indices
    val x get() = entries[0]
    val y get() = if (entries.size >= 2) entries[1] else throw DimensionException("Vector $this does not have a y coordinate")
    val z get() = if (entries.size >= 3) entries[2] else throw DimensionException("Vector $this does not have a z coordinate")
    val w get() = if (entries.size >= 4) entries[3] else throw DimensionException("Vector $this does not have a w coordinate")

    /**
     * Returns the length of this vector
     */
    val length: Double
        get() = sqrt(this * this)

    fun crossProduct(other: Vec): Vec {
        if (this.height != 3 || other.height != 3) throw DimensionException("Cannot calculate cross product between $this and $other")
        return Vec(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x)
    }

    /**
     * Returns the part of this vector that is parallel to the given vector.
     * The returned vector is safe to use.
     *
     * @param ontoThisOne the returned vector is parallel to this one
     * @return the part of this vector that is parallel to the given vector
     */
    fun projectOnto(ontoThisOne: Vec): Vec {
        return ontoThisOne * (this * ontoThisOne) / (ontoThisOne * ontoThisOne)
    }

    fun setTo(other: Vec) {
        if (height != other.height) throw DimensionException("$this can not be set to $other")
        for (i in entries.indices) {
            this[i] = other[i]
        }
    }

    fun angleWith(other: Vec): Double {
        return acos(other * this / (other.length * this.length))
    }

    /**
     * Returns true if the angle between this vector and the given one is smaller than 90°. Otherwise, returns false.
     */
    fun hasSharpAngleTo(other: Vec): Boolean = abs(angleWith(other)) < PI / 2

    val height get() = entries.size

    override fun iterator(): Iterator<Double> = entries.iterator()

    fun isEmpty() = entries.isEmpty()

    override fun equals(other: Any?): Boolean {
        if (other !is Vec) return false
        if (other.height != this.height) return false
        return (this - other).entries.all { abs(it) < 0.00001 }
    }

    override fun toString(): String {
        return entries.joinToString(
            prefix = "(",
            postfix = ")",
            separator = ", "
        ) { it.format(digitsAfterComma = 2).removeSuffix(".00") }
    }

    override fun hashCode(): Int {
        return entries.contentHashCode()
    }

    constructor(vararg entries: Number) : this(entries.toList().map { it.toDouble() }.toTypedArray())
    constructor(size: Int, operation: (i: Int) -> Number) : this(Array(size) { operation(it).toDouble() })

    companion object {
        val ZERO_3 = Vec(0, 0, 0)
        val ones_3 = Vec(1, 1, 1)
        fun random(size: Int) = Vec(size) { random() }
        fun zero(size: Int) = Vec(size) { _ -> 0 }
        fun ones(size: Int) = Vec(size) { _ -> 1 }
    }
}
