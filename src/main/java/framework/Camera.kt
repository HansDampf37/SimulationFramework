package framework

import algebra.*
import physics.Meters
import java.lang.Math.PI
import kotlin.math.*

/**
 * Projects 3d coordinates into 2d Space with the [project]-method. As opposed to [Graphics3d] the projection
 * is not orthographic. Furthermore, the camera can be placed at arbitrary positions and orientations in space.
 * Further material: [Lecture about Camera transformation](https://www.cse.psu.edu/~rtc12/CSE486/lecture12.pdf),
 * [Lecture 2](https://www.cse.psu.edu/~rtc12/CSE486/lecture13.pdf),
 * [Rotation Matrices](https://en.wikipedia.org/wiki/Rotation_matrix)
 * @param x position coordinate
 * @param y position coordinate
 * @param z position coordinate
 * @param turnAngle
 * @param nodAngle
 * @param focalLength distance from pinhole to projection plane
 * @param widthPixels resolution of projection plane
 * @param heightPixels resolution of projection plane
 */
class Camera(
    x: Double, y: Double, z: Double,
    turnAngle: Double, nodAngle: Double,
    var zoomX: Double, var zoomY: Double,
    private var focalLength: Double,
    var widthPixels: Int,
    var heightPixels: Int,
) : Point3d(x, y, z) {
    var yaw: Double = 0.0
    var pitch: Double = 0.0
    var roll: Double = 0.0

    var turnAngle: Double = 0.0
        set(value) {
            yaw = (-abs(nodAngle) / (PI / 2) + 1) * value
            roll = abs(nodAngle) / (PI / 2) * value
            field = value
        }

    var nodAngle: Double
        get() = pitch
        set(value) {
            pitch = if (value > PI / 2) PI / 2
            else if (value < -PI / 2) -PI / 2
            else value
            yaw = (-abs(nodAngle) / (PI / 2) + 1) * turnAngle
            roll = abs(nodAngle) / (PI / 2) * turnAngle
        }

    init {
        this.turnAngle = turnAngle
        this.nodAngle = nodAngle
    }

    val lookingDirection: Vec
        get() {
            val v: Vec4 = matrixYawInv * (matrixPitchInv * (matrixRollInv * Vec4(0.0, 0.0, 1.0, 1.0)))
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
            cos(yaw) * sin(pitch) * sin(roll + PI) - sin(yaw) * cos(roll + PI),
            cos(yaw) * sin(pitch) * cos(roll + PI) + sin(yaw) * sin(roll + PI),
            0.0,
            sin(yaw) * cos(pitch),
            sin(yaw) * sin(pitch) * sin(roll + PI) + cos(yaw) * cos(roll + PI),
            sin(yaw) * sin(pitch) * cos(roll + PI) - cos(yaw) * sin(roll + PI),
            0.0,
            -sin(pitch),
            cos(pitch) * sin(roll + PI),
            cos(pitch) * cos(roll + PI),
            0.0,
            0.0,
            0.0,
            0.0,
            1.0
        )

    private val matrixPitch
        get() = Matrix4X4(
            1.0, 0.0, 0.0, 0.0,
            0.0, cos(pitch), -sin(pitch), 0.0,
            0.0, sin(pitch), cos(pitch), 0.0,
            0.0, 0.0, 0.0, 1.0
        )

    private val matrixPitchInv
        get() = Matrix4X4(
            1.0, 0.0, 0.0, 0.0,
            0.0, cos(-pitch), -sin(-pitch), 0.0,
            0.0, sin(-pitch), cos(-pitch), 0.0,
            0.0, 0.0, 0.0, 1.0
        )

    private val matrixYaw
        get() = Matrix4X4(
            cos(yaw), 0.0, sin(yaw), 0.0,
            0.0, 1.0, 0.0, 0.0,
            -sin(yaw), 0.0, cos(yaw), 0.0,
            0.0, 0.0, 0.0, 1.0
        )

    private val matrixYawInv
        get() = Matrix4X4(
            cos(-yaw), 0.0, sin(-yaw), 0.0,
            0.0, 1.0, 0.0, 0.0,
            -sin(-yaw), 0.0, cos(-yaw), 0.0,
            0.0, 0.0, 0.0, 1.0
        )

    private val matrixRoll
        get() = Matrix4X4(
            cos(roll + PI), -sin(roll + PI), 0.0, 0.0,
            sin(roll + PI), cos(roll + PI), 0.0, 0.0,
            0.0, 0.0, 1.0, 0.0,
            0.0, 0.0, 0.0, 1.0
        )

    private val matrixRollInv
        get() = Matrix4X4(
            cos(-roll - PI), -sin(-roll - PI), 0.0, 0.0,
            sin(-roll - PI), cos(-roll - PI), 0.0, 0.0,
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