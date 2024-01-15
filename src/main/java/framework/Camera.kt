package framework

import algebra.*
import physics.Meters
import kotlin.math.cos
import kotlin.math.sin

/**
 * Projects 3d coordinates into 2d Space with the [project]-method. As opposed to [Graphics3d] the projection
 * is not orthographic. Furthermore, the camera can be placed at arbitrary positions and orientations in space.
 * Further material: [Lecture about Camera transformation](https://www.cse.psu.edu/~rtc12/CSE486/lecture12.pdf),
 * [Lecture 2](https://www.cse.psu.edu/~rtc12/CSE486/lecture13.pdf),
 * [Rotation Matrices](https://en.wikipedia.org/wiki/Rotation_matrix)
 * @param x position coordinate
 * @param y position coordinate
 * @param z position coordinate
 * @param yaw rotation around y-axis
 * @param pitch camera rotating up and down
 * @param roll camera rotating around its [lookingDirection]
 * @param focalLength distance from pinhole to projection plane
 * @param widthPixels resolution of projection plane
 * @param heightPixels resolution of projection plane
 */
class Camera(
    x: Double, y: Double, z: Double,
    yaw: Double, pitch: Double, private var roll: Double,
    var zoomX: Double, var zoomY: Double,
    private var focalLength: Double,
    var widthPixels: Int,
    var heightPixels: Int,
) : Point3d(x, y, z) {

    var yaw: Double = yaw
        set(value) {
            field = value
            roll = field - pitch
        }

    var pitch: Double = pitch
        set(value) {
            field = value
            roll = yaw - field
        }


    val lookingDirection: Vec
        get() {
            val v: Vec4 = matrixYaw * (matrixPitch * (matrixRoll * Vec4(0.0, 0.0, 1.0, 1.0)))
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

    private val matrixRoll
        get() = Matrix4X4(
            1.0, 0.0, 0.0, 0.0,
            0.0, cos(roll), -sin(roll), 0.0,
            0.0, sin(roll), cos(roll), 0.0,
            0.0, 0.0, 0.0, 1.0
        )

    private val matrixYaw
        get() = Matrix4X4(
            cos(yaw), 0.0, sin(yaw), 0.0,
            0.0, 1.0, 0.0, 0.0,
            -sin(yaw), 0.0, cos(yaw), 0.0,
            0.0, 0.0, 0.0, 1.0
        )

    private val matrixPitch
        get() = Matrix4X4(
            cos(pitch), -sin(pitch), 0.0, 0.0,
            sin(pitch), cos(pitch), 0.0, 0.0,
            0.0, 0.0, 1.0, 0.0,
            0.0, 0.0, 0.0, 1.0
        )

    private val cameraToPixelCoords
        get() = Matrix3x4(
            focalLength / zoomX, 0.0, widthPixels / 2.0, 0.0,
            0.0, focalLength / zoomY, heightPixels / 2.0, 0.0,
            0.0, 0.0, 1.0, 0.0
        )

    fun project(v: Vec): Pair<Vec2, Meters> {
        if ((v - this.positionVector).hasSharpAngleTo(lookingDirection)) {
            val vHom = Vec4(v.x, v.y, v.z, 1.0)
            val cameraCoordinate = matrixYaw * (matrixPitch * (matrixRoll * (translationMatrix4x4 * vHom)))
            val filmCoords = cameraToPixelCoords * cameraCoordinate
            val filmX = if (filmCoords.z != 0.0) filmCoords.x / filmCoords.z else filmCoords.x
            val filmY = if (filmCoords.z != 0.0) filmCoords.y / filmCoords.z else filmCoords.y
            return Pair(Vec2(filmX, filmY), (this - v).length)
        }
        return Pair(Vec2(-1.0, -1.0), Double.NEGATIVE_INFINITY)
    }
}