package framework

import algebra.Vec
import algebra.Vec2
import java.awt.Color
import java.awt.image.BufferedImage
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt


class BoundingBox(var minX: Int, var minY: Int, var maxX: Int, var maxY: Int)

class InterpolationResult(
    val inPrimitive: Boolean,
    val depth: Float,
    val color: Vec,
    val normal: Vec
)

class Vertex(
    var position: Vec,
    var color: Vec,
    var normal: Vec,
    var screenPosition: Vec2? = null,
    var depth: Float = -1f,
)

class Triangle(val v1: Vertex, val v2: Vertex, val v3: Vertex)
class Line(val v1: Vertex, val v2: Vertex)
class Circle(val v1: Vertex, val radius: Float)
class TriangleStrip(val vertices: List<Vertex> = ArrayList())

class Rasterizer(val camera: Camera) {
    var image: BufferedImage =
        BufferedImage(camera.screenWidth, camera.screenHeight, BufferedImage.TYPE_INT_RGB) // image puffer
    private var zBuffer: FloatArray =
        FloatArray(camera.screenWidth * camera.screenHeight) // Z-buffer to store depth values

    init {
        prepareForNewFrame()
    }

    fun rasterizeLine(line: Line) {
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
                if (depth < zBuffer[index]) {
                    // Update z-buffer
                    zBuffer[index] = depth
                    // interpolate color
                    val color = (1 - t) * line.v1.color + t * line.v2.color
                    val pixelColor = color.x.toInt() or color.y.toInt().shl(8) or color.z.toInt().shl(16)
                    // Set pixel color in the image buffer (using line color or other criteria)
                    image.setRGB(x0, y0, pixelColor)
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


    fun rasterizeTriangle(triangle: Triangle) {
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
                    (alpha * v1.normal + beta * v2.normal + gamma * v3.normal)
                )
            } else {
                InterpolationResult(false, -1f, Vec(0.0, 0.0, 0.0), Vec(0.0, 0.0, 0.0))
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
            val bb = BoundingBox(minX, minY, maxX, maxY)
            return bb
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
        for (y in bb.minY..bb.maxY) {
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
                    if (depth < zBuffer[index]) {
                        // Update z-buffer
                        zBuffer[index] = depth

                        // Interpolate color
                        val pixelColor = interpolation.color.x.toInt() or interpolation.color.y.toInt()
                            .shl(8) or interpolation.color.z.toInt().shl(16)

                        // Set pixel color in the image buffer
                        image.setRGB(x, y, pixelColor)
                    }
                }
            }
        }
    }

    fun rasterizeCircle(circle: Circle) {
        fun interpolateDepthColorNormal(v1: Vertex, pixel: Vec2, radius: Float): InterpolationResult {
            // Check if the point is inside the sphere
            val dx = pixel.x.toFloat() - v1.screenPosition!!.x.toFloat()
            val dy = pixel.y.toFloat() - v1.screenPosition!!.y.toFloat()
            val distanceFromCenterSquared = dx * dx + dy * dy
            val radiusSquared = radius * radius

            return if (distanceFromCenterSquared <= radiusSquared) {
                // Point is inside the sphere
                // Calculate the depth adjustment based on the distance from the sphere's center
                val depthAdjustment = sqrt(radiusSquared - distanceFromCenterSquared)

                // Calculate the normal vector by normalizing the vector pointing from the sphere's center to the surface point
                val sphereCenterToSurface = Vec(dx, dy, sqrt(radiusSquared - distanceFromCenterSquared))
                val normal = sphereCenterToSurface.normalize()

                // Simple shading: Use dot product with light direction
                val shadingFactor = maxOf(0.0, normal * Vec(0.0, 0.0, 1.0))

                InterpolationResult(
                    inPrimitive = true,
                    depth = v1.depth - 0.1f * depthAdjustment,
                    color = v1.color * shadingFactor,
                    normal = normal
                )
            } else {
                InterpolationResult(false, -1f, Vec(0.0, 0.0, 0.0), Vec(0.0, 0.0, 0.0))
            }
        }
        // Convert 3D coordinates to 2D screen space
        val (pixelCoordinate, distance) = camera.project(circle.v1.position)
        circle.v1.screenPosition = pixelCoordinate
        circle.v1.depth = distance.toFloat()
        if (distance < 0) return

        val radius = circle.radius * camera.focalLength / (distance * camera.zoom)
        val bb = BoundingBox(
            (circle.v1.screenPosition!!.x - radius).toInt(),
            (circle.v1.screenPosition!!.y - radius).toInt(),
            (circle.v1.screenPosition!!.x + radius).toInt(),
            (circle.v1.screenPosition!!.y + radius).toInt()
        )

        // Clip bounding box to image bounds
        bb.minX = max(0, bb.minX)
        bb.minY = max(0, bb.minY)
        bb.maxX = min((image.width - 1), bb.maxX)
        bb.maxY = min((image.height - 1), bb.maxY)

        // Iterate over pixels in the bounding box
        for (y in bb.minY..bb.maxY) {
            for (x in bb.minX..bb.maxX) {
                val interpolation = interpolateDepthColorNormal(circle.v1, Vec2(x.toDouble(), y.toDouble()), radius.toFloat())
                // Check if the current pixel is inside the triangle
                if (interpolation.inPrimitive) {
                    // pixel is inside
                    // Check against z-buffer
                    val index = y * camera.screenWidth + x
                    if (interpolation.depth < zBuffer[index]) {
                        // Update z-buffer
                        zBuffer[index] = interpolation.depth
                        val color = interpolation.color
                        val pixelColor = color.x.toInt().shl(16) or color.y.toInt().shl(8) or color.z.toInt()

                        // Set pixel color in the image buffer
                        image.setRGB(x, y, pixelColor)
                    }
                }
            }
        }
    }

    fun rasterizeTriangleStrip(triangleStrip: TriangleStrip) {
        // Iterate through the vertices, creating triangles on the fly
        for (i in 0 until triangleStrip.vertices.size - 2) {
            val v1 = triangleStrip.vertices[i]
            val v2 = triangleStrip.vertices[i + 1]
            val v3 = triangleStrip.vertices[i + 2]

            rasterizeTriangle(Triangle(v1, v2, v3))
        }
    }

    fun prepareForNewFrame() {
        val graphics = image.graphics
        graphics.color = Color.BLACK
        graphics.drawRect(0, 0, image.width, image.height)
        Arrays.fill(zBuffer, Float.MAX_VALUE) // fill z puffer with maximum value
    }

    fun updateWidthHeightFromCamera() {
        image = BufferedImage(camera.screenWidth, camera.screenHeight, BufferedImage.TYPE_INT_RGB)
        zBuffer = FloatArray(camera.screenWidth * camera.screenHeight)
        prepareForNewFrame()
    }
}

operator fun Double.times(v: Vec) = v * this
operator fun Float.times(v: Vec) = v * this.toDouble()
operator fun Int.times(v: Vec) = v * this.toDouble()
