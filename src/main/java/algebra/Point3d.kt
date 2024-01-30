package algebra

open class Point3d(open var x: Double, open var y: Double, open var z: Double) {

    constructor(positionVector: Vec): this(positionVector.x, positionVector.y, positionVector.z)

    fun add(delta: Vec) {
        x += delta.x
        y += delta.y
        z += delta.z
    }

    operator fun plus(delta: Vec) = positionVector + delta
    operator fun minus(delta: Vec) = positionVector - delta


    fun set(positionVector: Vec) {
        x = positionVector.x
        y = positionVector.y
        z = positionVector.z
    }

    fun getConnectingVectorTo(other: Point3d): Vec {
        return Vec(other.x - x, other.y - y, other.z - z)
    }

    fun getDirectionTo(other: Point3d): Vec {
        val result = getConnectingVectorTo(other)
        return result.normalize()
    }

    fun getDirectionToPointAt(other: Vec): Vec {
        val result = positionVector - other
        return result.normalize()
    }

    fun getDistanceTo(other: Point3d): Double {
        return getConnectingVectorTo(other).length
    }

    fun getDistanceToPointAt(other: Vec): Double {
        return (positionVector - other).length
    }

    val positionVector: Vec
        get() = Vec(x, y, z)

    override fun toString(): String {
        return "[" + x.toInt() + ", " + y.toInt() + ", " + z.toInt() + "]"
    }
}