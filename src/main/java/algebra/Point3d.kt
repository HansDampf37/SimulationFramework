package algebra

open class Point3d(open var x: Double, open var y: Double, open var z: Double) {

    constructor(positionVector: Vec3): this(positionVector.x, positionVector.y, positionVector.z)

    fun add(delta: Vec3) {
        x += delta.x
        y += delta.y
        z += delta.z
    }

    operator fun plus(delta: Vec3) = positionVector + delta
    operator fun minus(delta: Vec3) = positionVector - delta


    fun set(positionVector: Vec3) {
        x = positionVector.x
        y = positionVector.y
        z = positionVector.z
    }

    fun getConnectingVectorTo(other: Point3d): Vec3 {
        return Vec3(other.x - x, other.y - y, other.z - z)
    }

    fun getDirectionTo(other: Point3d): Vec3 {
        val result = getConnectingVectorTo(other)
        return result.normalize()
    }

    fun getDirectionToPointAt(other: Vec3): Vec3 {
        val result = positionVector - other
        return result.normalize()
    }

    fun getDistanceTo(other: Point3d): Double {
        return getConnectingVectorTo(other).length
    }

    fun getDistanceToPointAt(other: Vec3): Double {
        return (positionVector - other).length
    }

    val positionVector: Vec3
        get() = Vec3(x, y, z)

    override fun toString(): String {
        return "[" + x.toInt() + ", " + y.toInt() + ", " + z.toInt() + "]"
    }
}