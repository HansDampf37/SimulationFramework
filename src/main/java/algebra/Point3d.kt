package algebra

open class Point3d(open var x: Double, open var y: Double, open var z: Double) {

    constructor(positionVector: Vec3BLablabla): this(positionVector.x, positionVector.y, positionVector.z)

    fun add(delta: Vec3BLablabla) {
        x += delta.x
        y += delta.y
        z += delta.z
    }

    operator fun plus(delta: Vec3BLablabla) = positionVector + delta
    operator fun minus(delta: Vec3BLablabla) = positionVector - delta


    fun set(positionVector: Vec3BLablabla) {
        x = positionVector.x
        y = positionVector.y
        z = positionVector.z
    }

    fun getConnectingVectorTo(other: Point3d): Vec3BLablabla {
        return Vec3BLablabla(other.x - x, other.y - y, other.z - z)
    }

    fun getDirectionTo(other: Point3d): Vec3BLablabla {
        val result = getConnectingVectorTo(other)
        return result.normalize()
    }

    fun getDirectionToPointAt(other: Vec3BLablabla): Vec3BLablabla {
        val result = positionVector - other
        return result.normalize()
    }

    fun getDistanceTo(other: Point3d): Double {
        return getConnectingVectorTo(other).length
    }

    fun getDistanceToPointAt(other: Vec3BLablabla): Double {
        return (positionVector - other).length
    }

    val positionVector: Vec3BLablabla
        get() = Vec3BLablabla(x, y, z)

    override fun toString(): String {
        return "[" + x.toInt() + ", " + y.toInt() + ", " + z.toInt() + "]"
    }
}