package framework

import algebra.Vec
import org.junit.jupiter.api.Test
import java.awt.FlowLayout
import java.lang.Thread.sleep
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel


class RasterizerTest {
    private val camera = Camera(0.0, 0.0, 0.0,
    1.0, 1.0,
    10.0,
    1000, 600)
    private val rasterizer = Rasterizer(camera)

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
        val triangle1 = Triangle(arrayOf(v1, v2, v3))
        val v4 = Vertex(position = Vec(-10.0, 10.0, 1.0), color = c4, normal = zero)
        val v5 = Vertex(position = Vec(-10.0, -10.0, 2.0), color = c5, normal = zero)
        val v6 = Vertex(position = Vec(10.0, 10.0, 1.0), color = c6, normal = zero)
        val triangle2 = Triangle(arrayOf(v4, v5, v6))
        val line = Line(arrayOf(v1, v4))
        rasterizer.renderPrimitive(triangle1)
        rasterizer.renderPrimitive(triangle2)
        rasterizer.renderPrimitive(line)

        val frame = JFrame()
        frame.contentPane.setLayout(FlowLayout())
        frame.contentPane.add(JLabel(ImageIcon(rasterizer.image)))
        frame.pack()
        frame.isVisible = true
        sleep(20000)
    }
}