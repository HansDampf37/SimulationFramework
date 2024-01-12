package framework

import algebra.Matrix3x4
import algebra.Matrix4X4
import algebra.Point3d
import algebra.Vec
import algebra.Vec4
import kotlin.math.cos
import kotlin.math.sin

/**
 * Projects 3d coordinates into 2d Space. As opposed to [Graphics3d] the projection is not orthographic. Furthermore,
 * the camera can be placed at arbitrary positions and orientations in space.
 * Further material: [Lecture about Camera transformation](https://www.cse.psu.edu/~rtc12/CSE486/lecture12.pdf),
 * [Lecture 2](https://www.cse.psu.edu/~rtc12/CSE486/lecture13.pdf),
 * [Rotation Matrices](https://en.wikipedia.org/wiki/Rotation_matrix)
 */
class Camera(
    x: Double, y: Double, z: Double,
    val yaw: Double, val pitch: Double, val roll: Double,
    var zoomX: Double, var zoomY: Double,
    private var focalLength: Double,
    var widthPixels: Int,
    var heightPixels: Int
) : Point3d(x, y, z) {

    val lookingDirection: Vec
        get() {
            val v: Vec4 = rotationMatrix4x4 * Vec4(1.0, 0.0, 0.0, 1.0)
            return Vec(v.x, v.y, v.z)
        }

    var zoom: Double
        get() = zoomX
        set(value) {
            zoomX = value
            zoomY = value
        }
    private val translationMatrix4x4
        get() = Matrix4X4(
            1.0, 0.0, 0.0, -x,
            0.0, 1.0, 0.0, -y,
            0.0, 0.0, 1.0, -z,
            0.0, 0.0, 0.0, 1.0
        )

    private val cameraToPixelCoords
        get() = Matrix3x4(
            focalLength / zoomX, 0.0, widthPixels / 2.0, 0.0,
            0.0, focalLength / zoomY, heightPixels / 2.0, 0.0,
            0.0, 0.0, 1.0, 0.0
        )

    private val rotationMatrix4x4
        get() = Matrix4X4(
            cos(yaw) * cos(pitch),
            cos(yaw) * sin(pitch) * sin(roll) - sin(yaw) * cos(roll),
            cos(yaw) * sin(pitch) * cos(roll) + sin(yaw) * sin(roll),
            0.0,
            sin(yaw) * cos(pitch),
            sin(yaw) * sin(pitch) * sin(roll) + cos(yaw) * cos(roll),
            sin(yaw) * sin(pitch) * cos(roll) - cos(yaw) * sin(roll),
            0.0,
            -sin(pitch),
            cos(pitch) * sin(roll),
            cos(pitch) * cos(roll),
            0.0,
            0.0,
            0.0,
            0.0,
            1.0
        )

    fun project(v: Vec): Pair<Double, Double> {
        //if ((v - this.positionVector).hasSharpAngleTo(lookingDirection)) {
        val vHom = Vec4(v.x, v.y, v.z, 1.0)
        val cameraCoordinate = rotationMatrix4x4 * (translationMatrix4x4 * vHom)
        val filmCoords = cameraToPixelCoords * cameraCoordinate
        val filmX = filmCoords.x / filmCoords.z
        val filmY = filmCoords.y / filmCoords.z
        return Pair(filmX, filmY)
        //}
        //return Pair(-1.0, -1.0)
    }

    /*private val yaw
       get() = atan2(lookingDirection.normalize().x, lookingDirection.normalize().z)

   private val pitch
       get() = asin(-lookingDirection.normalize().y)

   private val roll
       get() = 0.0*/
}