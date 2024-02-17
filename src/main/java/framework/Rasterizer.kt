package framework

import Conf
import algebra.Vec3
import algebra.Vec2
import algebra.Vec4
import framework.interfaces.Entity
import times
import toColor
import toIntColor
import java.awt.GradientPaint
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.util.*
import kotlin.math.*


class BoundingBox(var minX: Int, var minY: Int, var maxX: Int, var maxY: Int)

class InterpolationResult(
    val inPrimitive: Boolean,
    val depth: Float,
    val color: Vec3,
    val normal: Vec3,
    val inOutline: Boolean
)

class Vertex(
    var position: Vec3,
    var color: Vec3,
    var normal: Vec3,
    var screenPosition: Vec2? = null,
    var depth: Float = -1f,
)

class Triangle(val v1: Vertex, val v2: Vertex, val v3: Vertex)
class Line(val v1: Vertex, val v2: Vertex)
class Circle(val v1: Vertex, val radius: Float)
class TriangleStrip(val vertices: List<Vertex> = ArrayList())

class Rasterizer(val camera: Camera) {
    /**
     * Calls to rasterization methods like [rasterizeSphere], [rasterizeLine], [rasterizeTriangle],
     * or [rasterizeTriangleStrip] draw the respective primitives on this [BufferedImage] which can then be retrieved.
     * To reset the image along with internal logic use [newFrame].
     */
    var image: BufferedImage =
        BufferedImage(camera.screenWidth, camera.screenHeight, BufferedImage.TYPE_INT_RGB) // image puffer
        private set
    private var zBuffer: FloatArray =
        FloatArray(camera.screenWidth * camera.screenHeight) // Z-buffer to store depth values
    private var entityPuffer = arrayOfNulls<Entity>(camera.screenWidth * camera.screenHeight)

    init {
        newFrame()
    }

    fun rasterizeLine(line: Line, entity: Entity?) {
        // Convert 3D coordinates to 2D screen space
        for (v in arrayOf(line.v1, line.v2)) {
            val (p, d) = camera.project(v.position)
            v.screenPosition = p
            v.depth = d.toFloat()
            if (d < 0) return
        }

        var x0 = line.v1.screenPosition!!.x.toInt()
        var y0 = line.v1.screenPosition!!.y.toInt()
        val x1 = line.v2.screenPosition!!.x.toInt()
        val y1 = line.v2.screenPosition!!.y.toInt()

        val dx = abs(x1 - x0)
        val dy = -abs(y1 - y0)
        val sx = if (x0 < x1) 1 else -1
        val sy = if (y0 < y1) 1 else -1

        var err = dx + dy

        while (true) {
            // Draw the pixel at (x0, y0)
            // Check if the pixel is within the screen bounds
            if (x0 >= 0 && x0 < camera.screenWidth && y0 >= 0 && y0 < camera.screenHeight) {
                // Interpolate depth
                val t: Float = if (dx == 0) 0f else (x0 - line.v1.screenPosition!!.x).toFloat() / dx.toFloat()
                val depth = (1 - t) * line.v1.depth + t * line.v2.depth
                // Check against z-buffer
                val index = y0 * camera.screenWidth + x0
                if (depth >= 0 && depth < zBuffer[index]) {
                    // Update z-buffer
                    zBuffer[index] = depth
                    entityPuffer[index] = entity
                    // interpolate color
                    val color = ((1 - t) * line.v1.color + t * line.v2.color)
                    // Set pixel color in the image buffer (using line color or other criteria)
                    image.setRGB(x0, y0, color.toIntColor())
                }
            }

            if (x0 == x1 && y0 == y1) {
                break
            }

            val e2 = 2 * err
            if (e2 >= dy) {
                err += dy
                x0 += sx
            }
            if (e2 <= dx) {
                err += dx
                y0 += sy
            }
        }
    }

    fun rasterizeTriangle(triangle: Triangle, entity: Entity?){
        fun interpolateDepthColorNormal(v1: Vertex, v2: Vertex, v3: Vertex, pixel: Vec2): InterpolationResult {
            val p0: Vec2 = v1.screenPosition!!
            val p1: Vec2 = v2.screenPosition!!
            val p2: Vec2 = v3.screenPosition!!
            // Compute barycentric coordinates
            val denominator: Double = (p1.y - p2.y) * (p0.x - p2.x) + (p2.x - p1.x) * (p0.y - p2.y)
            val alpha: Double = ((p1.y - p2.y) * (pixel.x - p2.x) + (p2.x - p1.x) * (pixel.y - p2.y)) / denominator
            val beta: Double = ((p2.y - p0.y) * (pixel.x - p2.x) + (p0.x - p2.x) * (pixel.y - p2.y)) / denominator
            val gamma = 1 - alpha - beta
            // Check if the point is inside the triangle
            return if (alpha >= 0 && beta >= 0 && gamma >= 0 && alpha <= 1 && beta <= 1 && gamma <= 1) {
                // point is inside -> Interpolate depth using barycentric coordinates
                InterpolationResult(
                    true,
                    (alpha * v1.depth + beta * v2.depth + gamma * v3.depth).toFloat(),
                    (alpha * v1.color + beta * v2.color + gamma * v3.color),
                    (alpha * v1.normal + beta * v2.normal + gamma * v3.normal),
                    (alpha == 0.0 || alpha == 1.0) && (beta == 0.0 || beta == 1.0) || (gamma == 0.0 || gamma == 1.0)
                )
            } else {
                InterpolationResult(false, -1f, Vec3(0.0, 0.0, 0.0), Vec3(0.0, 0.0, 0.0), false)
            }
        }

        fun boundingBox(v1: Vertex, v2: Vertex, v3: Vertex): BoundingBox {
            val pixel1 = v1.screenPosition!!
            val pixel2 = v2.screenPosition!!
            val pixel3 = v3.screenPosition!!
            val minX = min(pixel1.x, min(pixel2.x, pixel3.x)).toInt()
            val minY = min(pixel1.y, min(pixel2.y, pixel3.y)).toInt()
            val maxX = max(pixel1.x, max(pixel2.x, pixel3.x)).toInt()
            val maxY = max(pixel1.y, max(pixel2.y, pixel3.y)).toInt()
            return BoundingBox(minX, minY, maxX, maxY)
        }
        // Convert 3D coordinates to 2D screen space
        for (v in arrayOf(triangle.v1, triangle.v2, triangle.v3)) {
            val (p, d) = camera.project(v.position)
            v.screenPosition = p
            v.depth = d.toFloat()
            if (d < 0) return
        }

        val bb = boundingBox(triangle.v1, triangle.v2, triangle.v3)

        // Clip bounding box to image bounds
        bb.minX = max(0, bb.minX)
        bb.minY = max(0, bb.minY)
        bb.maxX = min((image.width - 1), bb.maxX)
        bb.maxY = min((image.height - 1), bb.maxY)

        // Iterate over pixels in the bounding box
        IntRange(bb.minY, bb.maxY).toList().parallelStream().forEach { y ->
            for (x in bb.minX..bb.maxX) {
                // Check if the current pixel is inside the triangle
                val interpolation = interpolateDepthColorNormal(
                    triangle.v1, triangle.v2, triangle.v3, Vec2(x.toDouble(), y.toDouble())
                )
                if (interpolation.inPrimitive) {
                    // Interpolate depth value
                    val depth = interpolation.depth

                    // Check against z-buffer
                    val index = y * camera.screenWidth + x
                    if (depth >= 0 && depth < zBuffer[index]) {
                        // Update z-buffer
                        zBuffer[index] = depth
                        entityPuffer[index] = entity

                        if (entity?.outlineRasterization == true && interpolation.inOutline) {
                            image.setRGB(x, y, Conf.colorScheme.colorOutline.rgb)
                        } else {
                            // Set pixel color in the image buffer
                            image.setRGB(x, y, interpolation.color.toIntColor())
                        }
                    }
                }
            }
        }
    }

    fun rasterizeSphere(circle: Circle, entity: Entity?) {
        fun interpolateDepthColorNormal(
            v1: Vertex, pixel: Vec2, radiusPixel: Int, radiusWorldCoords: Float
        ): InterpolationResult {
            if (radiusPixel == 0) return InterpolationResult(false, -1f, Vec3.zero, Vec3.zero, false)
            // Check if the point is inside the sphere
            val dxPixels = pixel.x.toFloat() - v1.screenPosition!!.x.toFloat()
            val dyPixels = pixel.y.toFloat() - v1.screenPosition!!.y.toFloat()
            val distanceFromCenterSquaredPixels = dxPixels * dxPixels + dyPixels * dyPixels
            val radiusSquaredPixels = radiusPixel * radiusPixel

            return if (distanceFromCenterSquaredPixels <= radiusSquaredPixels) {
                // Point is inside the sphere
                // Calculate the depth adjustment based on the distance from the sphere's center
                val depthAdjustmentPixels = sqrt(radiusSquaredPixels - distanceFromCenterSquaredPixels)
                val depthAdjustment = depthAdjustmentPixels / radiusPixel * radiusWorldCoords

                // Calculate the normal vector by normalizing the vector pointing from the sphere's center to the surface point
                val sphereCenterToSurface = Vec3(dxPixels, dyPixels, depthAdjustmentPixels)
                val normal = sphereCenterToSurface.normalize()

                // Simple shading: Use dot product with light direction
                val shadingFactor = if (Conf.shadingOnSpheres) {
                    val lightDirHom = (camera.rotateCameraToWorld * Vec4(0.0, 0.0, -1.0, 1.0))
                    val lightDir = Vec3(lightDirHom.x, lightDirHom.y, lightDirHom.z)
                    0.3 + 0.7 * maxOf(0.0, normal * lightDir)
                } else {
                    1.0
                }

                InterpolationResult(
                    inPrimitive = true,
                    depth = v1.depth - depthAdjustment,
                    color = v1.color * shadingFactor,
                    normal = normal,
                    radiusPixel - sqrt(distanceFromCenterSquaredPixels) <= 2
                )
            } else {
                InterpolationResult(false, -1f, Vec3(0.0, 0.0, 0.0), Vec3(0.0, 0.0, 0.0), false)
            }
        }
        // Convert 3D coordinates to 2D screen space
        val (pixelCoordinate, distance) = camera.project(circle.v1.position)
        circle.v1.screenPosition = pixelCoordinate
        circle.v1.depth = distance.toFloat()
        if (distance < 0) return

        val radiusPixels = (circle.radius * camera.focalLength / (distance * camera.zoom)).toInt()
        val bb = BoundingBox(
            (circle.v1.screenPosition!!.x - radiusPixels).toInt(),
            (circle.v1.screenPosition!!.y - radiusPixels).toInt(),
            (circle.v1.screenPosition!!.x + radiusPixels).toInt(),
            (circle.v1.screenPosition!!.y + radiusPixels).toInt()
        )

        // Clip bounding box to image bounds
        bb.minX = max(0, bb.minX)
        bb.minY = max(0, bb.minY)
        bb.maxX = min((image.width - 1), bb.maxX)
        bb.maxY = min((image.height - 1), bb.maxY)

        // Iterate over pixels in the bounding box
        for (y in bb.minY..bb.maxY) {
            for (x in bb.minX..bb.maxX) {
                val interpolation = interpolateDepthColorNormal(
                    circle.v1,
                    Vec2(x.toDouble(), y.toDouble()),
                    radiusPixels,
                    circle.radius
                )
                // Check if the current pixel is inside the triangle
                if (interpolation.inPrimitive) {
                    // pixel is inside
                    // Check against z-buffer
                    val index = y * camera.screenWidth + x
                    if (interpolation.depth >= 0 && interpolation.depth < zBuffer[index]) {
                        // Update z-buffer
                        zBuffer[index] = interpolation.depth
                        entityPuffer[index] = entity
                        val color = interpolation.color

                        if (entity?.outlineRasterization == true && interpolation.inOutline) {
                            image.setRGB(x, y, Conf.colorScheme.colorOutline.rgb)
                        } else {
                            // Set pixel color in the image buffer
                            image.setRGB(x, y, color.toIntColor())
                        }
                    }
                }
            }
        }
    }

    fun rasterizeTriangleStrip(triangleStrip: TriangleStrip, entity: Entity?) {
        // Iterate through the vertices, creating triangles on the fly
        for (i in 0 until triangleStrip.vertices.size - 2) {
            val v1 = triangleStrip.vertices[i]
            val v2 = triangleStrip.vertices[i + 1]
            val v3 = triangleStrip.vertices[i + 2]

            rasterizeTriangle(Triangle(v1, v2, v3), entity)
        }
    }

    fun rasterizePixel(point: Vec3, color: Vec3, entity: Entity?) {
        val (screenPosition, depth) = camera.project(point)
        if (depth < 0) return
        val x = screenPosition.x.toInt()
        val y = screenPosition.y.toInt()
        if (x < 0 || x >= image.width) return
        if (y < 0 || y >= image.height) return
        val index = y * image.width + x
        if (depth <= zBuffer[index]) {
            image.setRGB(x, y, color.toIntColor())
            entityPuffer[index] = entity
            zBuffer[index] = depth.toFloat()
        }
    }

    /**
     * Resets the [image] + z-puffer. Furthermore, for each pixel coordinate [getEntityAt] returns null again.
     */
    fun newFrame() {
        val graphics = image.graphics as Graphics2D
        graphics.color = Conf.background_color
        drawBackgroundGradientBasedOnCameraRotation(graphics)
        Arrays.fill(zBuffer, Float.MAX_VALUE) // fill z puffer with maximum value
        Arrays.fill(entityPuffer, null)
    }

    private fun drawBackgroundGradientBasedOnCameraRotation(g: Graphics2D) {
        val alpha = atan(camera.screenHeight * camera.zoom / camera.focalLength)
        val lowerAngle = max(camera.theta - alpha, 0.0)
        val upperAngle = min(camera.theta + alpha, PI)
        // create gradient from color scheme
        val gradient = Conf.colorScheme.horizon.gradient(0.0, PI)
        // clip only to visible angle range
        gradient.clip(lowerAngle, upperAngle)
        // map from angle range to range from 0 to image height
        gradient.translateScale(0.0, image.height.toDouble())
        for (i in 0 until gradient.size - 1) {
            val fromY = image.height.toFloat() - gradient[i].second
            val toY = image.height.toFloat() - gradient[i + 1].second
            // Ensure that fromY is greater than toY
            val startY = if (fromY < toY) fromY else toY
            val height = abs(fromY - toY)
            val gradientPaint = GradientPaint(
                0f, fromY.toFloat(), gradient[i].first,
                0f, toY.toFloat() + 1, gradient[i + 1].first
            )
            g.paint = gradientPaint
            g.fillRect(0, startY.toInt(), image.width, height.toInt() + 1)
        }
    }


    /**
     * When called the [image] and additional internal variables are resized to match the [camera]'s
     * screen's width and height.
     */
    fun updateWidthHeightFromCamera() {
        image = BufferedImage(camera.screenWidth, camera.screenHeight, BufferedImage.TYPE_INT_RGB)
        zBuffer = FloatArray(camera.screenWidth * camera.screenHeight) { Float.MAX_VALUE }
        entityPuffer = arrayOfNulls(camera.screenWidth * camera.screenHeight)
    }

    /**
     * Returns the [Entity] drawn at the specified coordinates. If no entity is drawn at the specified location,
     * null is returned. In order to associate drawn pixels with entities the entities must be passed via the
     * rasterization methods. If an entity is drawn at the specified coordinates, but it was not passed as an argument
     * to the respective rasterization method, null is returned.
     * @see [rasterizeTriangleStrip]
     * @see [rasterizeSphere]
     * @see [rasterizeLine]
     * @see [rasterizeTriangle]
     */
    fun getEntityAt(x: Int, y: Int): Entity? {
        return entityPuffer[y * camera.screenWidth + x]
    }

    /**
     * Returns a [BufferedImage] where pixels are white if and only if the pixel is on top of the specified [Entity].
     * @param entity the specified entity
     * @return a mask for this entity
     */
    fun entityMask(entity: Entity): BufferedImage {
        val maskImage = BufferedImage(camera.screenWidth, camera.screenHeight, BufferedImage.TYPE_INT_RGB)
        for (x in 0 until camera.screenWidth) {
            for (y in 0 until camera.screenHeight) {
                if (getEntityAt(x, y) == entity) {
                    maskImage.setRGB(x, y, 0b111111111111111111111111)
                }
            }
        }
        return maskImage
    }

    fun depthMask(entity: Entity?): BufferedImage {
        val maskImage = BufferedImage(camera.screenWidth, camera.screenHeight, BufferedImage.TYPE_BYTE_GRAY)
        for (x in 0 until camera.screenWidth) {
            for (y in 0 until camera.screenHeight) {
                val entityAtXY = getEntityAt(x, y)
                if (entity == null || entityAtXY == entity) {
                    val dist = zBuffer[y * camera.screenWidth + x]
                    maskImage.setRGB(x, y, (-dist).toInt())
                }
            }
        }
        return maskImage
    }
}