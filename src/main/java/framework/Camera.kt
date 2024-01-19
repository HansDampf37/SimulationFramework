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
 * @param screenWidth resolution of projection plane
 * @param screenHeight resolution of projection plane
 */
class Camera(
    x: Double, y: Double, z: Double,
    turnAngle: Double, nodAngle: Double,
    zoomX: Double, zoomY: Double,
    focalLength: Double,
    screenWidth: Int,
    screenHeight: Int,
) : Point3d(x, y, z) {
    var gamma: Double = 0.0
        set(value) {
            projectionMatrixIsValid = false
            field = value
        }
    var beta: Double = 0.0
        set(value) {
            projectionMatrixIsValid = false
            field = value
        }
    var alpha: Double = 0.0
        set(value) {
            projectionMatrixIsValid = false
            field = value
        }

    override var x: Double = x
        set(value) {
            projectionMatrixIsValid = false
            field = value
        }

    override var y: Double = y
        set(value) {
            projectionMatrixIsValid = false
            field = value
        }

    override var z: Double = z
        set(value) {
            projectionMatrixIsValid = false
            field = value
        }

    var zoomX: Double = zoomX
        set(value) {
            projectionMatrixIsValid = false
            field = value
        }

    var zoomY: Double = zoomY
        set(value) {
            projectionMatrixIsValid = false
            field = value
        }

    var focalLength: Double = focalLength
        set(value) {
            projectionMatrixIsValid = false
            field = value
        }

    var screenWidth: Int = screenWidth
        set(value) {
            projectionMatrixIsValid = false
            field = value
        }

    var screenHeight: Int = screenHeight
        set(value) {
            projectionMatrixIsValid = false
            field = value
        }

    var turnAngle: Double = 0.0
        set(value) {
            gamma = (-abs(nodAngle) / (PI / 2) + 1) * value
            alpha = abs(nodAngle) / (PI / 2) * value
            field = value
            projectionMatrixIsValid = false
        }

    var nodAngle: Double
        get() = beta
        set(value) {
            beta = if (value > PI / 2) PI / 2
            else if (value < -PI / 2) -PI / 2
            else value
            gamma = (-abs(turnAngle) / (PI / 2) + 1) * turnAngle
            alpha = abs(turnAngle) / (PI / 2) * turnAngle
            projectionMatrixIsValid = false
        }

    init {
        this.turnAngle = turnAngle
        this.nodAngle = nodAngle
    }

    val lookingDirection: Vec
        get() {
            val v: Vec4 = matrixRollInv * (matrixPitchInv * (matrixYawInv * Vec4(0.0, 0.0, 1.0, 1.0)))
            return Vec(v.x, v.y, v.z).normalize()
        }

    val up: Vec
        get() {
            val v: Vec4 = matrixRollInv * (matrixPitchInv * (matrixYawInv * Vec4(0.0, 1.0, 0.0, 1.0)))
            return Vec(v.x, v.y, v.z).normalize()
        }

    val left: Vec
        get() {
            val v: Vec4 = matrixRollInv * (matrixPitchInv * (matrixYawInv * Vec4(1.0, 0.0, 0.0, 1.0)))
            return Vec(v.x, v.y, v.z).normalize()
        }

    var zoom: Double
        get() = zoomX
        set(value) {
            zoomX = value
            zoomY = value
            projectionMatrixIsValid = false
        }
    private val translationMatrix4x4
        get() = Matrix4X4(
            1.0, 0.0, 0.0, -x,
            0.0, 1.0, 0.0, -y,
            0.0, 0.0, 1.0, -z,
            0.0, 0.0, 0.0, 1.0
        )

    private val matrixPitch
        get() = Matrix4X4(
            1.0, 0.0, 0.0, 0.0,
            0.0, cos(beta), -sin(beta), 0.0,
            0.0, sin(beta), cos(beta), 0.0,
            0.0, 0.0, 0.0, 1.0
        )

    private val matrixPitchInv
        get() = Matrix4X4(
            1.0, 0.0, 0.0, 0.0,
            0.0, cos(-beta), -sin(-beta), 0.0,
            0.0, sin(-beta), cos(-beta), 0.0,
            0.0, 0.0, 0.0, 1.0
        )

    private val matrixYaw
        get() = Matrix4X4(
            cos(gamma), 0.0, sin(gamma), 0.0,
            0.0, 1.0, 0.0, 0.0,
            -sin(gamma), 0.0, cos(gamma), 0.0,
            0.0, 0.0, 0.0, 1.0
        )

    private val matrixYawInv
        get() = Matrix4X4(
            cos(-gamma), 0.0, sin(-gamma), 0.0,
            0.0, 1.0, 0.0, 0.0,
            -sin(-gamma), 0.0, cos(-gamma), 0.0,
            0.0, 0.0, 0.0, 1.0
        )

    private val matrixRoll
        get() = Matrix4X4(
            cos(alpha + PI), -sin(alpha + PI), 0.0, 0.0,
            sin(alpha + PI), cos(alpha + PI), 0.0, 0.0,
            0.0, 0.0, 1.0, 0.0,
            0.0, 0.0, 0.0, 1.0
        )

    private val matrixRollInv
        get() = Matrix4X4(
            cos(-alpha + PI), -sin(-alpha + PI), 0.0, 0.0,
            sin(-alpha + PI), cos(-alpha + PI), 0.0, 0.0,
            0.0, 0.0, 1.0, 0.0,
            0.0, 0.0, 0.0, 1.0
        )

    private val cameraToPixelCoords
        get() = Matrix3x4(
            -focalLength / zoomX, 0.0, screenWidth / 2.0, 0.0,
            0.0, -focalLength / zoomY, screenHeight / 2.0, 0.0,
            0.0, 0.0, 1.0, 0.0
        )

    private var projectionMatrix: Matrix3x4 = Matrix3x4(1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0)
    private var projectionMatrixIsValid = false

    fun project(v: Vec): Pair<Vec2, Meters> {
        if ((v - this.positionVector).angleWith(lookingDirection) <= PI / 2) {
            val vHom = Vec4(v.x, v.y, v.z, 1.0)
            if (!projectionMatrixIsValid) {
                projectionMatrix = cameraToPixelCoords * matrixYaw * matrixPitch * matrixRoll * translationMatrix4x4
                projectionMatrixIsValid = true
            }
            val filmCoords = projectionMatrix * vHom
            // translate and rotate world coordinate system in camera coordinate system
            // val cameraCoordinate = matrixYaw * (matrixPitch * (matrixRoll * (translationMatrix4x4 * vHom)))
            // project camera coordinates into film coordinates
            // val filmCoords = cameraToPixelCoords * cameraCoordinate
            val filmX = if (filmCoords.z != 0.0) filmCoords.x / filmCoords.z else filmCoords.x
            val filmY = if (filmCoords.z != 0.0) filmCoords.y / filmCoords.z else filmCoords.y
            return Pair(Vec2(filmX, filmY), (this - v).length)
        }
        return Pair(Vec2(-1.0, -1.0), Double.NEGATIVE_INFINITY)
    }
}