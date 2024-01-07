package spacesimulation

import spacesimulation.algebra.Point3d
import spacesimulation.algebra.Vec
import java.awt.Color
import java.awt.Graphics
import kotlin.math.cos

class Graphics3d @JvmOverloads constructor(
    cameraAngleHorizontal: Double = Math.PI / 4,
    cameraAngleVertical: Double = Math.PI / 4,
    zoom: Double = 0.3
) {
    private var cameraAngleHorizontal = Math.PI / 4
    private var cameraAngleVertical = Math.PI / 4
    private var zoom: Double
    private var originX = 0.0
    private var originY = 0.0
    private var height = 0

    init {
        this.cameraAngleHorizontal = cameraAngleHorizontal
        this.cameraAngleVertical = cameraAngleVertical
        this.zoom = zoom
    }

    fun drawLine(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double, g: Graphics) {
        g.drawLine(calcX(x1, y1, z1), calcY(x1, y1, z1), calcX(x2, y2, z2), calcY(x2, y2, z2))
    }

    fun drawLine(a: Point3d, b: Point3d, g: Graphics) {
        drawLine(a.x, a.y, a.z, b.x, b.y, b.z, g)
    }

    fun drawDot(x: Double, y: Double, z: Double, radius: Int, color: Color?, g: Graphics) {
        g.color = color
        g.fillOval(calcX(x, y, z) - radius, calcY(x, y, z) - radius, 2 * radius, 2 * radius)
    }

    fun drawDot(a: Point3d, radius: Int, color: Color?, g: Graphics) {
        drawDot(a.x, a.y, a.z, radius, color, g)
    }

    private fun calcX(x: Double, y: Double, z: Double): Int {
        return ((x * cos(cameraAngleHorizontal) + z * cos(Math.PI / 2 + cameraAngleHorizontal)) * zoom
                + originX).toInt()
    }

    private fun calcY(x: Double, y: Double, z: Double): Int {
        return height - ((y * cos(cameraAngleVertical) + z * cos(Math.PI / 2 + cameraAngleVertical) * cos(
            cameraAngleHorizontal
        ) + x * cos(-Math.PI / 2 + cameraAngleVertical) * cos(cameraAngleHorizontal + Math.PI / 2))
                * zoom + originY).toInt()
    }

    // private int calcX(Point3d a) {
    //     return calcX(a.x, a.y, a.z);
    // }
    // private int calcY(Point3d a) {
    //     return calcY(a.x, a.y, a.z);
    // }
    fun fillTriangle(
        x1: Double,
        y1: Double,
        z1: Double,
        x2: Double,
        y2: Double,
        z2: Double,
        x3: Double,
        y3: Double,
        z3: Double,
        color: Color?,
        g: Graphics
    ) {
        g.color = Color.black
        drawLine(x1, y1, z1, x2, y2, z2, g)
        drawLine(x2, y2, z2, x3, y3, z3, g)
        drawLine(x3, y3, z3, x1, y1, z1, g)
        g.color = color
        val xValues = intArrayOf(calcX(x1, y1, z1), calcX(x2, y2, z2), calcX(x3, y3, z3))
        val yValues = intArrayOf(calcY(x1, y1, z1), calcY(x2, y2, z2), calcY(x3, y3, z3))
        g.fillPolygon(xValues, yValues, 3)
    }

    fun fillTriangle(a: Point3d, b: Point3d, c: Point3d, color: Color?, g: Graphics) {
        fillTriangle(a.x, a.y, a.z, b.x, b.y, b.z, c.x, c.y, c.z, color, g)
    }

    fun fillParallelogram(a: Point3d, delta1: Vec, delta2: Vec, color: Color?, g: Graphics) {
        fillParallelogram(
            a.x,
            a.y,
            a.z,
            a.x + delta1.x,
            a.y + delta1.y,
            a.z + delta1.z,
            a.x + delta1.x + delta2.x,
            a.y + delta1.y + delta2.y,
            a.z + delta1.z + delta2.z,
            a.x + delta2.x,
            a.y + delta2.y,
            a.z + delta2.z,
            color,
            g
        )
    }

    fun fillParallelogram(a: Point3d, b: Point3d, c: Point3d, d: Point3d, color: Color?, g: Graphics) {
        fillParallelogram(a.x, a.y, a.z, b.x, b.y, b.z, c.x, c.y, c.z, d.x, d.y, d.z, color, g)
    }

    fun fillParallelogram(
        x1: Double,
        y1: Double,
        z1: Double,
        x2: Double,
        y2: Double,
        z2: Double,
        x3: Double,
        y3: Double,
        z3: Double,
        x4: Double,
        y4: Double,
        z4: Double,
        color: Color?,
        g: Graphics
    ) {
        g.color = Color.black
        drawLine(x1, y1, z1, x2, y2, z2, g)
        drawLine(x2, y2, z2, x3, y3, z3, g)
        drawLine(x3, y3, z3, x4, y4, z4, g)
        drawLine(x4, y4, z4, x1, y1, z1, g)
        g.color = color
        val xValues = intArrayOf(calcX(x1, y1, z1), calcX(x2, y2, z2), calcX(x3, y3, z3), calcX(x4, y4, z4))
        val yValues = intArrayOf(calcY(x1, y1, z1), calcY(x2, y2, z2), calcY(x3, y3, z3), calcY(x4, y4, z4))
        g.fillPolygon(xValues, yValues, 4)
    }

    fun moveHorizontalCamera(dir: Int) {
        if (dir > 0) cameraAngleHorizontal += 0.003 else if (dir < 0) cameraAngleHorizontal -= 0.003
    }

    fun moveVerticalCamera(dir: Int) {
        if (dir > 0) cameraAngleVertical += 0.003 else if (dir < 0) cameraAngleVertical -= 0.003
        if (cameraAngleVertical > Math.PI / 2) cameraAngleVertical = Math.PI / 2
        if (cameraAngleVertical < -Math.PI / 2) cameraAngleVertical = -Math.PI / 2
    }

    fun zoom(dir: Int) {
        try {
            zoom *= if (dir > 0) 1.005 else 0.995
        } catch (e: ArithmeticException) {
            //Too small or large
            println("Zoom limit reached")
        }
    }

    fun setWindowHeightAndWidth(width: Int, height: Int) {
        // this.width = width;
        this.height = height
        originX = (width / 2).toDouble()
        originY = (height / 2).toDouble()
    }

    fun setCameraAngleHorizontal(angle: Double) {
        cameraAngleHorizontal = angle
    }

    fun setCameraAngleVertical(angle: Double) {
        cameraAngleVertical = angle
    }

    fun setZoom(zoom: Double) {
        this.zoom = zoom
    }

    fun cameraSettingsToString(): String {
        return """Horizontal: ${(cameraAngleHorizontal / Math.PI * 180).toInt()} deg (${round(cameraAngleHorizontal / Math.PI)} * PI) 
Vertical: ${(cameraAngleVertical / Math.PI * 180).toInt()}deg (${round(cameraAngleVertical / Math.PI)} * PI) 
 Zoom: ${(zoom * 100).toInt()}%"""
    }

    private fun round(`in`: Double): Double {
        return (`in` * 100).toInt().toDouble() / 100.0
    }
}