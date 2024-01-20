package framework

import algebra.Vec
import algebra.Vec2
import java.awt.Color
import java.awt.image.BufferedImage
import java.util.*
import kotlin.math.max
import kotlin.math.min


interface Primitive {
    fun boundingBox(): BoundingBox

    fun interpolateDepthColorNormal(pixel: Vec2): InterpolationResult
    fun getVertices(): Array<Vertex>
}

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
class Triangle(private val vertices: Array<Vertex>): Primitive {

    override fun boundingBox(): BoundingBox {
        // Find bounding box of the triangle in screen space
        val pixel1 = vertices[0].screenPosition!!
        val pixel2 = vertices[1].screenPosition!!
        val pixel3 = vertices[2].screenPosition!!
        val minX = min(pixel1.x, min(pixel2.x, pixel3.x)).toInt()
        val minY = min(pixel1.y, min(pixel2.y, pixel3.y)).toInt()
        val maxX = max(pixel1.x, max(pixel2.x, pixel3.x)).toInt()
        val maxY = max(pixel1.y, max(pixel2.y, pixel3.y)).toInt()
        return BoundingBox(minX, minY, maxX, maxY)
    }

    override fun interpolateDepthColorNormal(pixel: Vec2): InterpolationResult {
        val v0: Vec2 = vertices[0].screenPosition!!
        val v1: Vec2 = vertices[1].screenPosition!!
        val v2: Vec2 = vertices[2].screenPosition!!
        // Compute barycentric coordinates
        val denominator: Double = (v1.y - v2.y) * (v0.x - v2.x) + (v2.x - v1.x) * (v0.y - v2.y)
        val alpha: Double = ((v1.y - v2.y) * (pixel.x - v2.x) + (v2.x - v1.x) * (pixel.y - v2.y)) / denominator
        val beta: Double = ((v2.y - v0.y) * (pixel.x - v2.x) + (v0.x - v2.x) * (pixel.y - v2.y)) / denominator
        val gamma = 1 - alpha - beta
        // Check if the point is inside the triangle
        return if (alpha >= 0 && beta >= 0 && gamma >= 0 && alpha <= 1 && beta <= 1 && gamma <= 1) {
            // point is inside -> Interpolate depth using barycentric coordinates
            InterpolationResult(
                true,
                (alpha * vertices[0].depth + beta * vertices[1].depth + gamma * vertices[2].depth).toFloat(),
                (alpha * vertices[0].color + beta * vertices[1].color + gamma * vertices[2].color),
                (alpha * vertices[0].normal + beta * vertices[1].normal + gamma * vertices[2].normal)
            )
        } else {
            InterpolationResult(false, -1f, Vec(0.0, 0.0, 0.0), Vec(0.0, 0.0, 0.0))
        }
    }

    override fun getVertices(): Array<Vertex> {
        return vertices
    }
}

class Line(private val vertices: Array<Vertex>): Primitive {
    override fun boundingBox(): BoundingBox {
        // Find bounding box of the triangle in screen space
        val pixel1 = vertices[0].screenPosition!!
        val pixel2 = vertices[1].screenPosition!!
        val minX = min(pixel1.x, pixel2.x).toInt()
        val minY = min(pixel1.y, pixel2.y).toInt()
        val maxX = max(pixel1.x, pixel2.x).toInt()
        val maxY = max(pixel1.y, pixel2.y).toInt()
        return BoundingBox(minX, minY, maxX, maxY)
    }

    override fun interpolateDepthColorNormal(pixel: Vec2): InterpolationResult {
        val v0: Vec2 = vertices[0].screenPosition!!
        val v1: Vec2 = vertices[1].screenPosition!!
        // Check if the point is on the line
        return if ((v1 - v0).normalize() * (pixel - v0).normalize() > 0.99) {
            // point is inside -> Interpolate depth normal and color
            val alpha = (pixel - v0).length() / (v1 - v0).length()
            val beta = 1 - alpha
            InterpolationResult(
                true,
                (alpha * vertices[0].depth + beta * vertices[1].depth).toFloat(),
                (alpha * vertices[0].color + beta * vertices[1].color),
                (alpha * vertices[0].normal + beta * vertices[1].normal)
            )
        } else {
            InterpolationResult(false, -1f, Vec(0.0, 0.0, 0.0), Vec(0.0, 0.0, 0.0))
        }
    }

    override fun getVertices(): Array<Vertex> {
        return vertices
    }
}

class Rasterizer(
    val camera: Camera
) {
    var image: BufferedImage = BufferedImage(camera.screenWidth, camera.screenHeight, BufferedImage.TYPE_INT_RGB) // image puffer
    private var zBuffer: FloatArray = FloatArray(camera.screenWidth * camera.screenHeight) // Z-buffer to store depth values

    init {
        prepareForNewFrame()
    }

    fun renderPrimitive(primitive: Primitive) {
        // Convert 3D coordinates to 2D screen space
        for (i in 0 until primitive.getVertices().size) {
            val vertex = primitive.getVertices()[i]
            val (p, d) = camera.project(vertex.position)
            vertex.screenPosition = p
            vertex.depth = d.toFloat()
            if (d < 0) return
        }

        val bb = primitive.boundingBox()

        // Clip bounding box to image bounds
        bb.minX = max(0, bb.minX)
        bb.minY = max(0, bb.minY)
        bb.maxX = min((image.width - 1), bb.maxX)
        bb.maxY = min((image.height - 1), bb.maxY)

        // Iterate over pixels in the bounding box
        for (y in bb.minY..bb.maxY) {
            for (x in bb.minX..bb.maxX) {
                // Check if the current pixel is inside the triangle
                val interpolation = primitive.interpolateDepthColorNormal(Vec2(x.toDouble(), y.toDouble()))
                if (interpolation.inPrimitive) {
                    // Interpolate depth value
                    val depth = interpolation.depth

                    // Check against z-buffer
                    val index = y * camera.screenWidth + x
                    if (depth < zBuffer[index]) {
                        // Update z-buffer
                        zBuffer[index] = depth

                        // Interpolate color
                        val pixelColor = interpolation.color.x.toInt() or interpolation.color.y.toInt().shl(8) or interpolation.color.y.toInt().shl(16)

                        // Set pixel color in the image buffer
                        image.setRGB(x, y, pixelColor)
                    }
                }
            }
        }
    }

    fun prepareForNewFrame() {
        val graphics = image.graphics
        graphics.color = Color.BLACK
        graphics.drawRect(0,0, image.width, image.height)
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
