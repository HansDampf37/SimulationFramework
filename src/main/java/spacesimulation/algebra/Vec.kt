package spacesimulation.algebra

import kotlin.math.sqrt

/**
 * Represents a Vector in 3d space.
 */
class Vec
/**
 * Constructor
 * @param deltaX x value
 * @param deltaY y value
 * @param deltaZ z value
 */(
    /**
     * x value
     */
    @JvmField var x: Double,
    /**
     * y value
     */
    @JvmField var y: Double,
    /**
     * z value
     */
    @JvmField var z: Double
) {
    /**
     * adds the given Vector to this Vector and returns this instance with updated values
     *
     * @param v other vector
     * @return the result o the vectoraddition with this vector and v
     */
    fun add(v: Vec): Vec {
        x += v.x
        y += v.y
        z += v.z
        return this
    }

    /**
     * subtracts the given Vector from this Vector and returns this instance with updated values
     *
     * @param v other vector
     * @return the result o the vectorsubtraction with this vector and v
     */
    fun sub(v: Vec): Vec {
        x -= v.x
        y -= v.y
        z -= v.z
        return this
    }

    val length: Double
        /**
         * Returns the length of this vector
         *
         * @return length
         */
        get() = sqrt(x * x + y * y + z * z)

    /**
     * Divides this vector through its length. This will keep its direction but make its length equal to 1. Then returns this instance with updated values.
     *
     * @return A vector with the same direction as this one had before, but length 1.
     */
    fun shortenToLengthOne(): Vec {
        val length = length
        if (length == 0.0) throw ArithmeticException("Division by zero")
        x /= length
        y /= length
        z /= length
        return this
    }

    /**
     * Returns the standard dot product (fundamental matrix = unit matrix)with the given vector.
     */
    fun scalarProduct(v: Vec): Double {
        return x * v.x + y * v.y + z * v.z
    }

    /**
     * Returns true if the angle between this vector and the given one is smaller than 90°. Otherwise, returns false.
     */
    fun hasSharpAngleTo(v: Vec): Boolean {
        val w = linearProjection(v)
        return w.x * v.x > 0 || w.y * v.y > 0 || w.z * v.z > 0
    }

    /**
     * Returns the part of this vector that is parallel to the given vector and updates the values of this instance to the parallel one's values.
     * The returned vector is safe to use.
     */
    fun linearProjection(ontoThisOne: Vec): Vec {
        val w = Vec(ontoThisOne.x, ontoThisOne.y, ontoThisOne.z)
        val factor = scalarProduct(w) / w.scalarProduct(w)
        w.scale(factor)
        x = w.x
        y = w.y
        z = w.z
        return this
    }

    /**
     * Scales this Vector by a given factor. Then returns this instance with updated values
     *
     * @param scalar factor
     * @return the result of scalar multiplication: this * scalar
     */
    fun scale(scalar: Double): Vec {
        x *= scalar
        y *= scalar
        z *= scalar
        return this
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Vec) {
            x == other.x && y == other.y && z == other.z
        } else false
    }

    override fun toString(): String {
        return "($x, $y, $z)"
    }

    companion object {
        /**
         * Returns the result of the vector addition v + w. The returned vector is safe to use.
         *
         * @param v first vector
         * @param w second vector
         * @return v + w
         */
        fun add(v: Vec, w: Vec): Vec {
            return Vec(v.x + w.x, v.y + w.y, v.z + w.z)
        }

        /**
         * Returns the result of the vector subtraction v - w. The returned vector is safe to use.
         *
         * @param first first vector
         * @param second second vector
         * @return v - w
         */
        fun sub(first: Vec, second: Vec): Vec {
            return Vec(first.x - second.x, first.y - second.y, first.z - second.z)
        }

        /**
         * Returns a vector with the same direction as the given one, but length 1. The returned vector is safe to use.
         *
         * @return a vector with the same direction as the given one, but length 1.
         */
        fun shortenToLengthOne(v: Vec): Vec {
            val length = v.length
            if (length == 0.0) throw ArithmeticException("Division by zero")
            return Vec(v.x / length, v.y / length, v.z / length)
        }

        /**
         * Returns the result of the scalar multiplication v * scalar without changing v. The returned vector is safe to use.
         * @param v Vector
         * @param scalar factor
         * @return the result of the scalar multiplication v * scalar
         */
        fun scale(v: Vec, scalar: Double): Vec {
            return Vec(v.x * scalar, v.y * scalar, v.z * scalar)
        }

        /**
         * Returns the part of this vector that is parallel to the given vector.
         * The returned vector is safe to use.
         *
         * @param willBeProjected the returned vector builds this one
         * @param ontoThisOne the returned vector is parallel to this one
         * @return the part of this vector that is parallel to the given vector
         */
        fun linearProjection(willBeProjected: Vec, ontoThisOne: Vec): Vec {
            if (ontoThisOne == Vec(0.0, 0.0, 0.0)) throw ArithmeticException("Division by zero")
            if (ontoThisOne.length == 0.0) return Vec(0.0, 0.0, 0.0)
            val w = Vec(ontoThisOne.x, ontoThisOne.y, ontoThisOne.z)
            val factor = willBeProjected.scalarProduct(w) / w.scalarProduct(w)
            w.scale(factor)
            return w
        }
    }
}