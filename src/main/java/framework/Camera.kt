package framework

import algebra.*
import physics.Meters
import java.awt.image.BufferedImage
import java.lang.Math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/**
 * Projects 3d coordinates into 2d Space with the [project]-method. As opposed to [Graphics3d] the projection
 * is not orthographic. Furthermore, the camera can be placed at arbitrary positions ([x], [y], [z]) and orientations
 * ([phi], [theta]) in space. This camera implements the pinhole camera model. In this model light is directed through
 * a hole. Behind this hole is the projection screen. The [focalLength] measures the distance between hole and screen.
 * Points in the world coordinate system are first translated and rotated in the camera coordinate system.
 * These points are then projected onto the screen through the hole.
 * Further material: [Lecture about Camera transformation](https://www.cse.psu.edu/~rtc12/CSE486/lecture12.pdf),
 * [Lecture 2](https://www.cse.psu.edu/~rtc12/CSE486/lecture13.pdf),
 * [Rotation matrices](https://en.wikipedia.org/wiki/Rodrigues%27_rotation_formula#Matrix_notation)
 * If [phi] and [theta] are both zero the camera should point at down at (0, 0, 1), the [up] vector should be (0, 1, 0)
 * and the [left] vector should point at (1, 0, 0)
 * @param x position coordinate
 * @param y position coordinate
 * @param z position coordinate
 * @param focalLength distance from pinhole to projection plane
 * @param screenWidth resolution of projection plane
 * @param screenHeight resolution of projection plane
 */
class Camera(
    x: Double, y: Double, z: Double,
    phi: Double = 0.0, theta: Double = 0.0,
    zoom: Double, focalLength: Double,
    screenWidth: Int, screenHeight: Int,
) : Point3d(x, y, z) {

    constructor(
        position: Vec,
        phi: Double = 0.0, theta: Double = 0.0,
        zoom: Double, focalLength: Double,
        screenWidth: Int, screenHeight: Int
    ) : this(
        position.x, position.y, position.z, phi, theta, zoom, focalLength, screenWidth, screenHeight
    )

    @WatchFloat("Speed", 0f, 10f)
    private val movementSpeed: Double = 5.0
    private val turningSpeed: Double = 5.0

    /**
     *  [phi] describes the ccw rotation around the z-axis from the x-axis from above.
     */
    @WatchDouble("Φ", -2 * PI, 2 * PI)
    var phi: Double = phi
        set(value) {
            projectionMatrixIsValid = false
            field = value
        }

    /**
     * [theta] describes the rotation around the [left]-vector from the z-axis from down (-PI / 2) to up (PI / 2).
     */
    @WatchDouble("θ", 0.0, PI)
    var theta: Double = theta
        set(value) {
            projectionMatrixIsValid = false
            field = min(PI, max(0.0, value))
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

    private var zoomX: Double = zoom
        set(value) {
            projectionMatrixIsValid = false
            field = value
        }

    private var zoomY: Double = zoom
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
            val v: Vec4 = (matrixThetaInv * (matrixPhiInv * Vec4(0.0, 0.0, 1.0, 1.0)))
            return Vec(v.x, v.y, v.z).normalize()
        }

    val up: Vec
        get() {
            val v: Vec4 = (matrixThetaInv * (matrixPhiInv * Vec4(0.0, 1.0, 0.0, 1.0)))
            return Vec(v.x, v.y, v.z).normalize()
        }

    val left: Vec
        get() = Vec(cos(phi), sin(phi), 0)

    var zoom: Double
        get() = zoomX
        set(value) {
            zoomX = value
            zoomY = value
        }

    init {
        if (zoom <= 0.0) throw IllegalArgumentException("Zoom must be > 0")
        if (focalLength <= 0.0) throw IllegalArgumentException("Focal length must be > 0")
    }

    private val translationMatrix4x4
        get() = Matrix4X4(
            1.0, 0.0, 0.0, -x,
            0.0, 1.0, 0.0, -y,
            0.0, 0.0, 1.0, -z,
            0.0, 0.0, 0.0, 1.0
        )

    private val matrixPhi: Matrix4X4
        get() {
            val k = Matrix4X4(
                0.0, 0.0, 1.0, 0.0,
                0.0, 0.0, 0.0, 0.0,
                -1.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 1.0
            )
            val I = Matrix4X4(
                1.0, 0.0, 0.0, 0.0,
                0.0, 1.0, 0.0, 0.0,
                0.0, 0.0, 1.0, 0.0,
                0.0, 0.0, 0.0, 1.0
            )
            return I + k * sin(phi) + k * k * (1 - cos(phi))
        }

    private val matrixTheta: Matrix4X4
        get() {
            // as we apply this transformation first, we rotate around the x-axis.
            val k = Matrix4X4(
                0.0, -left.z, left.y, 0.0,
                left.z, 0.0, -left.x, 0.0,
                -left.y, left.x, 0.0, 0.0,
                0.0, 0.0, 0.0, 1.0
            )
            val I = Matrix4X4(
                1.0, 0.0, 0.0, 0.0,
                0.0, 1.0, 0.0, 0.0,
                0.0, 0.0, 1.0, 0.0,
                0.0, 0.0, 0.0, 1.0
            )
            return I + k * sin(theta) + k * k * (1 - cos(theta))
        }

    private val matrixPhiInv: Matrix4X4
        get() = matrixPhi.transpose()

    private val matrixThetaInv: Matrix4X4
        get() = matrixTheta.transpose()

    /**
     * Maps a vector in the camera coordinate system to pixel coordinates
     */
    private val cameraToPixelCoords
        get() = Matrix3x4(
            -focalLength / zoomX, 0.0, screenWidth / 2.0, 0.0,
            0.0, -focalLength / zoomY, screenHeight / 2.0, 0.0,
            0.0, 0.0, 1.0, 0.0
        )

    /**
     * This variable is true if and only if all relevant properties
     * of the camera have not changed, since the last calculation of the [projectionMatrix].
     */
    private var projectionMatrixIsValid = false

    /**
     * Map world coordinates to pixel coordinates:
     * 1. translate vector with the [translationMatrix4x4]
     * 2. rotate around the [left] vector for [theta] with [matrixTheta]
     * 3. rotate around the z-axis for [phi] with [matrixPhi]
     * 4. map camera to pixel coordinates with [cameraToPixelCoords]
     * This matrix is cached in [projectionMatrix] meaning that if the camera position or rotation is not
     * changed this matrix is not recalculated but used again.
     */
    private var projectionMatrix: Matrix3x4 = cameraToPixelCoords * matrixPhi * matrixTheta * translationMatrix4x4
        get() {
            if (!projectionMatrixIsValid) {
                projectionMatrix = cameraToPixelCoords * matrixPhi * matrixTheta * translationMatrix4x4
                projectionMatrixIsValid = true
            }
            return field
        }
    private val rasterizer = Rasterizer(this)

    /**
     * Calls to rendering methods like [renderSphere], [renderLine], [renderTriangle],
     * or [renderStrip] draw the respective primitives on this [BufferedImage] which can then be retrieved.
     * To reset the image along with internal logic use [newFrame].
     */
    val image: BufferedImage
        get() = rasterizer.image

    fun moveForward(dt: Double = 1.0) = move(lookingDirection, dt)
    fun moveBackward(dt: Double = 1.0) = move(-lookingDirection, dt)
    fun moveUp(dt: Double = 1.0) = move(up, dt)
    fun moveDown(dt: Double = 1.0) = move(-up, dt)
    fun moveLeft(dt: Double = 1.0) = move(left, dt)
    fun moveRight(dt: Double = 1.0) = move(-left, dt)
    private fun move(direction: Vec, dt: Double) {
        add(direction.normalize() * movementSpeed * dt)
    }

    fun turn(dif: Vec2, dt: Double = 1.0) {
        val turnUp = dif.y * dt * turningSpeed
        val turnRight = dif.x * dt * turningSpeed
        phi += turnRight
        theta += turnUp

    }

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
    fun renderTriangle(v1: Vertex, v2: Vertex, v3: Vertex, entity: Entity? = null) =
        rasterizer.rasterizeTriangle(Triangle(v1, v2, v3), entity)

    /**
     * Draws a sphere on the [image] around the specified vertex and registers the entity on each pixel that is filled.
     * The entity can be retrieved by calling [getEntityAt]
     * @param v1 first [Vertex]
     * @param radius the radius of the sphere in world coordinates
     * @param entity the entity to register on each of the drawn pixels
     */
    fun renderSphere(v1: Vertex, radius: Float, entity: Entity? = null) =
        rasterizer.rasterizeSphere(Circle(v1, radius), entity)

    /**
     * Draws a triangle-strip on the [image] and registers the entity on each pixel that is filled.
     * The entity can be retrieved by calling [getEntityAt]
     * @param vertices list of vertices for the strip.
     * @param entity the entity to register on each of the drawn pixels
     */
    fun renderStrip(vertices: List<Vertex>, entity: Entity? = null) =
        rasterizer.rasterizeTriangleStrip(TriangleStrip(vertices), entity)

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