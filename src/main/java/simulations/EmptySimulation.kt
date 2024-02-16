package simulations

import algebra.Vec3
import framework.Simulation
import framework.Vertex
import framework.physics.Seconds
import java.awt.Color
import java.awt.Graphics

class EmptySimulation: Simulation("Test") {
    private val amountSquares = 50
    private val sizeSquares = 2.0

    init {
        reset()
    }
    override fun tick(dt: Seconds) = Unit

    fun render(g: Graphics) {
        val dx = Vec3(sizeSquares, 0.0, 0.0)
        val dy = Vec3(0.0, sizeSquares, 0.0)
        val dz = Vec3(0.0, 0.0, sizeSquares)
        val Z = dz * 50
        val Y = dy * 50
        val X = dx * 50
        for (x in 0 until amountSquares) {
            for (y in 0 until amountSquares) {
                var color = if ((y + x) % 2 == 0) Color.RED else Color.WHITE
                drawRect(dx * x + dy * y, dx * x + dx + dy * y, dx * x + dx + dy * y + dy, dy * y + dy + dx * x, color, g)
                color = if ((y + x) % 2 == 0) Color.PINK else Color.WHITE
                drawRect(dx * x + dy * y + Z, dx * x + dx + dy * y + Z, dx * x + dx + dy * y + dy + Z, dy * y + dy + dx * x + Z, color, g)
            }
        }

        for (x in 0 until amountSquares) {
            for (z in 0 until amountSquares) {
                var color = if ((z + x) % 2 == 0) Color.BLUE else Color.WHITE
                drawRect(dx * x + dz * z, dx * x + dx + dz * z, dx * x + dx + dz * z + dz, dz * z + dz + dx * x, color, g)
                color = if ((z + x) % 2 == 0) Color.CYAN else Color.WHITE
                drawRect(dx * x + dz * z + Y, dx * x + dx + dz * z + Y, dx * x + dx + dz * z + dz + Y, dz * z + dz + dx * x + Y, color, g)
            }
        }

        for (y in 0 until amountSquares) {
            for (z in 0 until amountSquares) {
                var color = if ((y + z) % 2 == 0) Color.GREEN else Color.WHITE
                drawRect(dy * y + dz * z, dy * y + dy + dz * z, dy * y + dy + dz * z + dz, dz * z + dz + dy * y, color, g)
                color = if ((y + z) % 2 == 0) Color.MAGENTA else Color.WHITE
                drawRect(dy * y + dz * z + X, dy * y + dz * z + dz + X, dy * y + dy + dz * z + dz + X, dz * z + dy * y + dy + X, color, g)
            }
        }
        // drawLookingDirection(g)
    }

    override fun render() {
        val dx = Vec3(sizeSquares, 0.0, 0.0)
        val dy = Vec3(0.0, sizeSquares, 0.0)
        val dz = Vec3(0.0, 0.0, sizeSquares)
        val Z = dz * 50
        val Y = dy * 50
        val X = dx * 50
        for (x in 0 until amountSquares) {
            for (y in 0 until amountSquares) {
                var color = if ((y + x) % 2 == 0) Vec3(255, 0, 0) else Vec3(255, 255, 255)
                camera.renderTriangle(
                    Vertex(dx * x + dy * y, color, Vec3.zero),
                    Vertex(dx * x + dx + dy * y, color, Vec3.zero),
                    Vertex(dx * x + dx + dy * y + dy, color, Vec3.zero))
                camera.renderTriangle(
                    Vertex(dx * x + dy * y, color, Vec3.zero),
                    Vertex(dx * x + dx + dy * y + dy, color, Vec3.zero),
                    Vertex(dy * y + dy + dx * x, color, Vec3.zero))
                color = if ((y + x) % 2 == 0) Vec3(255, 0, 0) else Vec3(255, 255, 255)
                camera.renderTriangle(
                    Vertex(dx * x + dy * y + Z, color, Vec3.zero),
                    Vertex(dx * x + dx + dy * y + Z, color, Vec3.zero),
                    Vertex(dx * x + dx + dy * y + dy + Z, color, Vec3.zero))
                camera.renderTriangle(
                    Vertex(dx * x + dy * y + Z, color, Vec3.zero),
                    Vertex(dx * x + dx + dy * y + dy + Z, color, Vec3.zero),
                    Vertex(dy * y + dy + dx * x + Z, color, Vec3.zero))
            }
        }
    }

    override fun reset() {
        camera.x = 50.0
        camera.y = 50.0
        camera.z = 50.0
        camera.focalLength = 1.0
        camera.zoom = 0.003
    }

    private fun drawLookingDirection(g: Graphics) {
        val p1 = camera.positionVector - camera.up + camera.lookingDirection * 0.1
        val p2 = camera.positionVector - camera.up + camera.lookingDirection * 10
        val (pr1, _) = camera.project(p1)
        val (pr2, _) = camera.project(p2)
        g.color = Color.black
        g.drawLine(pr1.x.toInt(), pr1.y.toInt(), pr2.x.toInt(), pr2.y.toInt())
    }

    fun drawRect(pos: Vec3, pos1: Vec3, pos2: Vec3, pos3: Vec3, color: Color, g: Graphics) {
        val (p1, d1) = camera.project(pos)
        val (p2, d2) = camera.project(pos1)
        val (p3, d3) = camera.project(pos2)
        val (p4, d4) = camera.project(pos3)
        val w = display.getWidth()
        val h = display.getHeight()
        if (p1.x < 0.0 && p2.x < 0.0 && p3.x < 0.0 && p4.x < 0.0) return
        if (p1.y < 0.0 && p2.y < 0.0 && p3.y < 0.0 && p4.y < 0.0) return
        if (p1.x > w && p2.x > w && p3.x > w && p4.x > w) return
        if (p1.y > h && p2.y > h && p3.y > h && p4.y > h) return
        if (d1 < 0 || d2 < 0 || d3 < 0 || d4 < 0) return
        val xPoints = intArrayOf(p1.x.toInt(), p2.x.toInt(), p3.x.toInt(), p4.x.toInt())
        val yPoints = intArrayOf(p1.y.toInt(), p2.y.toInt(), p3.y.toInt(), p4.y.toInt())
        g.color = color
        g.fillPolygon(xPoints, yPoints, 4)
    }
}