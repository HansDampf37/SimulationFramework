package framework

import algebra.Vec
import framework.interfaces.Entity
import framework.interfaces.Status
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import physics.Seconds
import java.awt.FlowLayout
import java.awt.image.BufferedImage
import java.lang.Thread.sleep
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel


class RasterizerTest {
    private val camera = Camera(
        x = 0.0, y = 0.0, z = 0.0,
        phi = 0.0, theta = 0.0,
        zoom = 0.01, focalLength = 1.0,
        1000, 600
    )
    private val rasterizer = Rasterizer(camera)

    @BeforeEach
    fun setup() {
        rasterizer.newFrame()
    }

    @Test
    fun testRasterization() {
        val zero = Vec(0.0, 0.0, 0.0)
        val c1 = Vec(0, 255, 0)
        val c2 = Vec(255, 0, 255)
        val c3 = Vec(255, 0, 0)
        val c4 = Vec(0, 255, 255)
        val c5 = Vec(255, 255, 0)
        val c6 = Vec(0, 0, 255)
        val v1 = Vertex(position = Vec(-10.0, -10.0, 1.0), color = c1, normal = zero)
        val v2 = Vertex(position = Vec(-10.0, 10.0, 2.0), color = c2, normal = zero)
        val v3 = Vertex(position = Vec(10.0, -10.0, 1.0), color = c3, normal = zero)
        val triangle1 = Triangle(v1, v2, v3)
        val v4 = Vertex(position = Vec(-10.0, 10.0, 1.0), color = c4, normal = zero)
        val v5 = Vertex(position = Vec(-10.0, -10.0, 2.0), color = c5, normal = zero)
        val v6 = Vertex(position = Vec(10.0, 10.0, 1.0), color = c6, normal = zero)
        val triangle2 = Triangle(v4, v5, v6)
        val line = Line(v1, v4)
        rasterizer.rasterizeTriangle(triangle1, null)
        rasterizer.rasterizeTriangle(triangle2, null)
        rasterizer.rasterizeLine(line, null)

        displayImage(rasterizer.image)
    }

    @Test
    fun testGetEntityTriangle() {
        val entity = object : Entity {
            override fun tick(dt: Seconds) = Unit
            override fun render(camera: Camera) = Unit
            override var outlineRasterization: Boolean = false
            override var color: Vec? = null
            override var position: Vec = Vec(0.0, 0.0, 0.0)
            override var velocity: Vec = Vec(0.0, 0.0, 0.0)
            override var acceleration: Vec = Vec(0.0, 0.0, 0.0)
            override var status: Status = Status.Movable
        }
        rasterizer.rasterizeTriangle(
            Triangle(
                Vertex(Vec(10.0, 20.0, -3.0), Vec.ones * 255, Vec.zero),
                Vertex(Vec(10.0, -20.0, 7.0), Vec.ones * 255, Vec.zero),
                Vertex(Vec(10.0, -35.0, -20.0), Vec.ones * 255, Vec.zero)
            ),
            entity
        )
        testEntityIsStoredInEachNonBlackPixel(entity)
    }

    @Test
    fun testGetEntitySphere() {
        val entity = object : Entity {
            override fun tick(dt: Seconds) = Unit
            override fun render(camera: Camera) = Unit
            override var outlineRasterization: Boolean = false
            override var color: Vec? = null
            override var position: Vec = Vec(0.0, 0.0, 0.0)
            override var velocity: Vec = Vec(0.0, 0.0, 0.0)
            override var acceleration: Vec = Vec(0.0, 0.0, 0.0)
            override var status: Status = Status.Movable
        }
        rasterizer.rasterizeSphere(
            Circle(
                Vertex(Vec(10.0, 0.0, 0.0), Vec.ones * 255, Vec.zero),
                9.0f
            ),
            entity
        )
        testEntityIsStoredInEachNonBlackPixel(entity)
    }

    private fun testEntityIsStoredInEachNonBlackPixel(entity: Entity) {
        val entityIsAt = BufferedImage(camera.screenWidth, camera.screenHeight, BufferedImage.TYPE_INT_RGB)
        val entityButNoPixel = BufferedImage(camera.screenWidth, camera.screenHeight, BufferedImage.TYPE_INT_RGB)
        val pixelButNoEntity = BufferedImage(camera.screenWidth, camera.screenHeight, BufferedImage.TYPE_INT_RGB)
        for (x in 0 until camera.screenWidth) {
            for (y in 0 until camera.screenHeight) {
                if (rasterizer.getEntityAt(x, y) == entity) {
                    entityIsAt.setRGB(x, y, 0b111111111111111111111111)
                    if (rasterizer.image.getRGB(x, y) == 0) {
                        entityButNoPixel.setRGB(x, y, 0b111111111111111111111111)
                    }
                } else {
                    if (rasterizer.image.getRGB(x, y) > 0) {
                        pixelButNoEntity.setRGB(x, y, 0b111111111111111111111111)
                    }
                }
            }
        }
    }

    @Test
    fun testDepthCircle() {
        rasterizer.rasterizeSphere(
            Circle(
                Vertex(10 * camera.lookingDirection, Vec.ones * 255, Vec.zero),
                9.0f
            ),
            null
        )
        val depthMask = rasterizer.depthMask(null)
        displayImage(depthMask)
    }

    private fun displayImage(image: BufferedImage) {
        val frame = JFrame()
        frame.contentPane.setLayout(FlowLayout())
        frame.contentPane.add(JLabel(ImageIcon(image)))
        frame.pack()
        frame.isVisible = true
        sleep(2000)
    }
}