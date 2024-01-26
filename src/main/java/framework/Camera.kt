package framework

import algebra.*
import physics.Meters
import java.awt.image.BufferedImage
import java.lang.IllegalArgumentException
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
 * @param focalLength distance from pinhole to projection plane
 * @param screenWidth resolution of projection plane
 * @param screenHeight resolution of projection plane
 */
class Camera(
    x: Double, y: Double, z: Double,
    zoomX: Double, zoomY: Double,
    focalLength: Double,
    screenWidth: Int,
    screenHeight: Int,
) : Point3d(x, y, z) {

    init {
        if (zoomX <= 0.0) throw IllegalArgumentException("Zoom must be > 0")
        if (zoomY <= 0.0) throw IllegalArgumentException("Zoom must be > 0")
        if (focalLength <= 0.0) throw IllegalArgumentException("Focal length must be > 0")
    }

    var phi: Double = 0.0
        set(value) {
            field = value
            updateYawPitchRoll()
        }
    var theta: Double = 0.0
        set(value) {
            field = value
            field = max(0.0, min(PI, field))
            updateYawPitchRoll()
        }

    // TODO TODO TODO
    private fun updateYawPitchRoll() {
        val dx = cos(phi) * sin(theta)
        val dy = sin(phi) * sin(theta)
        val dz = cos(theta)
        roll = if (dz >= 0) acos(dy / sqrt(dz * dz + dy * dy)) else 2 * PI - acos(dy / sqrt(dz * dz + dy * dy))
        pitch = if (dx >= 0) acos(dz / sqrt(dz * dz + dx * dx)) else 2 * PI - acos(dz / sqrt(dz * dz + dx * dx))
        yaw = if (dy >= 0) acos(dx / sqrt(dx * dx + dy * dy)) else 2 * PI - acos(dx / sqrt(dx * dx + dy * dy))
    }

    @WatchDouble("Yaw", 0.0, PI * 2)
    var yaw: Double = 0.0
        set(value) {
            projectionMatrixIsValid = false
            field = value
        }
    @WatchDouble("Pitch", 0.0, PI * 2)
    var pitch: Double = 0.0
        set(value) {
            projectionMatrixIsValid = false
            field = value
        }
    @WatchDouble("Roll", 0.0, PI * 2)
    var roll: Double = 0.0
        set(value) {
            projectionMatrixIsValid = false
            field = value
        }

    @WatchDouble("X", -100.0, 100.0)
    override var x: Double = x
        set(value) {
            projectionMatrixIsValid = false
            field = value
        }

    @WatchDouble("Y", -100.0, 100.0)
    override var y: Double = y
        set(value) {
            projectionMatrixIsValid = false
            field = value
        }

    @WatchDouble("Z", -100.0, 100.0)
    override var z: Double = z
        set(value) {
            projectionMatrixIsValid = false
            field = value
        }

    @WatchDouble("Zoom", 0.01, 10.0)
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

    @WatchDouble("Focal-Length", 0.01, 100.0)
    var focalLength: Double = focalLength
        set(value) {
            projectionMatrixIsValid = false
            field = value
        }

    var screenWidth: Int = screenWidth
        set(value) {
            projectionMatrixIsValid = false
            rasterizer.updateWidthHeightFromCamera()
            field = value
        }

    var screenHeight: Int = screenHeight
        set(value) {
            projectionMatrixIsValid = false
            rasterizer.updateWidthHeightFromCamera()
            field = value
        }

    val lookingDirection: Vec
        get() {
            val v: Vec4 = swpAxisInv * (matrixRollInv * (matrixPitchInv * (matrixYawInv * Vec4(0.0, 0.0, 1.0, 1.0))))
            return Vec(v.x, v.y, v.z).normalize()
        }

    val up: Vec
        get() {
            val v: Vec4 = swpAxisInv * (matrixRollInv * (matrixPitchInv * (matrixYawInv * Vec4(0.0, 1.0, 0.0, 1.0))))
            return Vec(v.x, v.y, v.z).normalize()
        }

    val left: Vec
        get() {
            val v: Vec4 = swpAxisInv * (matrixRollInv * (matrixPitchInv * (matrixYawInv * Vec4(1.0, 0.0, 0.0, 1.0))))
            return Vec(v.x, v.y, v.z).normalize()
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

    private val swpAxis
        get() = Matrix4X4(
            0.0, 1.0, 0.0, 0.0,
            0.0, 0.0, 1.0, 0.0,
            1.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 1.0
        )

    private val swpAxisInv
        get() = Matrix4X4(
            0.0, 0.0, 1.0, 0.0,
            1.0, 0.0, 0.0, 0.0,
            0.0, 1.0, 0.0, 0.0,
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
            cos(-roll + PI), -sin(-roll + PI), 0.0, 0.0,
            sin(-roll + PI), cos(-roll + PI), 0.0, 0.0,
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

    private val rasterizer = Rasterizer(this)

    /**
     * Calls to rendering methods like [renderSphere], [renderLine], [renderTriangle],
     * or [renderStrip] draw the respective primitives on this [BufferedImage] which can then be retrieved.
     * To reset the image along with internal logic use [newFrame].
     */
    val image: BufferedImage
        get() = rasterizer.image

    /**
     * Resets the [image]. Furthermore, for each pixel coordinate [getEntityAt] returns null again.
     */
    fun newFrame() = rasterizer.newFrame()

    /**
     * Projects the specified 3-dimensional vector to a 2-dimensional vector describing the pixel coordinates.
     * Additionally, the distance between [v] and the [Camera] is returned. If the pixel is behind the camera, the
     * distance is negative. It should be checked that the returned distance is positive before drawing the pixel to
     * avoid drawing objects that are behind the camera.
     *
     * @param v the 3-dimensional vector
     * @return pair containing the pixel coordinate and the distance from the camera
     */
    fun project(v: Vec): Pair<Vec2, Meters> {
        if ((v - this.positionVector).angleWith(lookingDirection) <= PI / 2) {
            val vHom = Vec4(v.x, v.y, v.z, 1.0)
            if (!projectionMatrixIsValid) {
                // translate and rotate world coordinate system in camera coordinate system
                // project camera coordinates into film coordinates
                projectionMatrix = cameraToPixelCoords * matrixYaw * matrixPitch * matrixRoll * swpAxis * translationMatrix4x4
                projectionMatrixIsValid = true
            }
            val filmCoords = projectionMatrix * vHom
            val filmX = if (filmCoords.z != 0.0) filmCoords.x / filmCoords.z else filmCoords.x
            val filmY = if (filmCoords.z != 0.0) filmCoords.y / filmCoords.z else filmCoords.y
            return Pair(Vec2(filmX, filmY), (this - v).length)
        }
        return Pair(Vec2(-1.0, -1.0), Double.NEGATIVE_INFINITY)
    }

    /**
     * Returns a string describing the cameras position, orientation and looking direction
     */
    fun cameraSettingsToString(): String {
        fun round(value: Double) = (value * 100).toInt().toDouble() / 100
        return "x: ${round(x)}, y: ${round(y)}, z: ${round(z)}, \n" +
                "yaw: ${round(yaw / PI)}π, pitch: ${round(pitch / PI)}π, roll: ${round(roll / PI)}π, \n" +
                "lookingDirection: [${round(lookingDirection.x)}, ${round(lookingDirection.y)}, ${round(lookingDirection.z)}]"
    }

    /**
     * Draws a line on the [image] between the specified vertices and registers the entity on each pixel that is filled.
     * The entity can be retrieved by calling [getEntityAt]
     * @param v1 first [Vertex]
     * @param v2 second [Vertex]
     * @param entity the entity to register on each of the drawn pixels
     */
    fun renderLine(v1: Vertex, v2: Vertex, entity: Entity? = null) = rasterizer.rasterizeLine(Line(v1, v2), entity)

    /**
     * Draws a triangle on the [image] between the specified vertices and registers the entity on each pixel that is filled.
     * The entity can be retrieved by calling [getEntityAt]
     * @param v1 first [Vertex]
     * @param v2 second [Vertex]
     * @param v3 second [Vertex]
     * @param entity the entity to register on each of the drawn pixels
     */
    fun renderTriangle(v1: Vertex, v2: Vertex, v3: Vertex, entity: Entity? = null) = rasterizer.rasterizeTriangle(Triangle(v1, v2, v3), entity)

    /**
     * Draws a sphere on the [image] around the specified vertex and registers the entity on each pixel that is filled.
     * The entity can be retrieved by calling [getEntityAt]
     * @param v1 first [Vertex]
     * @param radius the radius of the sphere in world coordinates
     * @param entity the entity to register on each of the drawn pixels
     */
    fun renderSphere(v1: Vertex, radius: Float, entity: Entity? = null) = rasterizer.rasterizeCircle(Circle(v1, radius), entity)

    /**
     * Draws a triangle-strip on the [image] and registers the entity on each pixel that is filled.
     * The entity can be retrieved by calling [getEntityAt]
     * @param vertices list of vertices for the strip.
     * @param entity the entity to register on each of the drawn pixels
     */
    fun renderStrip(vertices: List<Vertex>, entity: Entity? = null) = rasterizer.rasterizeTriangleStrip(TriangleStrip(vertices), entity)

    /**
     * Returns the [Entity] drawn at the specified coordinates. If no entity is drawn at the specified location,
     * null is returned. In order to associate drawn pixels with entities the entities must be passed via the
     * rendering methods. If an entity is drawn at the specified coordinates, but it was not passed as an argument
     * to the respective rasterization method, null is returned.
     * @see [renderStrip]
     * @see [renderSphere]
     * @see [renderLine]
     * @see [renderTriangle]
     */
    fun getEntityAt(x: Int, y: Int) = rasterizer.getEntityAt(x, y)
}