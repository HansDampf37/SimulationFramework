package algebra

import framework.Camera
import framework.Graphics3d
import java.awt.Color
import java.awt.Graphics

@Deprecated("TODO implement rendering with camera")
class CartesianCoordinateSystem(
    private val negative: Boolean, private val axisLength: Int, private val scaleSize: Double, private val color: Color
) {
    fun render(drawer: Graphics3d, g: Graphics) {
        g.color = color
        drawer.drawLine((if (negative) -axisLength else 0).toDouble(), 0.0, 0.0, axisLength.toDouble(), 0.0, 0.0, g)
        drawer.drawLine(0.0, (if (negative) -axisLength else 0).toDouble(), 0.0, 0.0, axisLength.toDouble(), 0.0, g)
        drawer.drawLine(0.0, 0.0, (if (negative) -axisLength else 0).toDouble(), 0.0, 0.0, axisLength.toDouble(), g)
        var i = 0
        while (i <= axisLength / scaleSize * if (negative) 2 else 1) {
            drawer.drawLine(
                (if (negative) -axisLength else 0) + i * scaleSize,
                -10.0,
                0.0,
                (if (negative) -axisLength else 0) + i * scaleSize,
                10.0,
                0.0,
                g
            )
            drawer.drawLine(
                -10.0,
                (if (negative) -axisLength else 0) + i * scaleSize,
                0.0,
                10.0,
                (if (negative) -axisLength else 0) + i * scaleSize,
                0.0,
                g
            )
            drawer.drawLine(
                0.0,
                -10.0,
                (if (negative) -axisLength else 0) + i * scaleSize,
                0.0,
                10.0,
                (if (negative) -axisLength else 0) + i * scaleSize,
                g
            )
            i++
        }
        g.color = Color.red
        drawer.drawLine(0.0, 0.0, 0.0, 0.0, 0.0, scaleSize, g)
        g.color = Color.green
        drawer.drawLine(0.0, 0.0, 0.0, 0.0, scaleSize, 0.0, g)
        g.color = Color.blue
        drawer.drawLine(0.0, 0.0, 0.0, scaleSize, 0.0, 0.0, g)
        var a = Point3d(0.0, 0.0, axisLength.toDouble())
        var b = Point3d(40.0, 0.0, (axisLength - 80).toDouble())
        var c = Point3d(-40.0, 0.0, (axisLength - 80).toDouble())
        drawer.fillTriangle(a, b, c, Color.red, g)
        b = Point3d(0.0, 40.0, (axisLength - 80).toDouble())
        c = Point3d(0.0, -40.0, (axisLength - 80).toDouble())
        drawer.fillTriangle(a, b, c, Color.red, g)
        drawer.drawLine(0.0, 0.0, axisLength.toDouble(), 0.0, 0.0, axisLength.toDouble(), g)
        a = Point3d(0.0, axisLength.toDouble(), 0.0)
        b = Point3d(0.0, (axisLength - 80).toDouble(), 40.0)
        c = Point3d(0.0, (axisLength - 80).toDouble(), -40.0)
        drawer.fillTriangle(a, b, c, Color.green, g)
        b = Point3d(40.0, (axisLength - 80).toDouble(), 0.0)
        c = Point3d(-40.0, (axisLength - 80).toDouble(), 0.0)
        drawer.fillTriangle(a, b, c, Color.green, g)
        drawer.drawLine(0.0, axisLength.toDouble(), 0.0, 0.0, axisLength.toDouble(), 0.0, g)
        a = Point3d(axisLength.toDouble(), 0.0, 0.0)
        b = Point3d((axisLength - 80).toDouble(), 40.0, 0.0)
        c = Point3d((axisLength - 80).toDouble(), -40.0, 0.0)
        drawer.fillTriangle(a, b, c, Color.blue, g)
        b = Point3d((axisLength - 80).toDouble(), 0.0, 40.0)
        c = Point3d((axisLength - 80).toDouble(), 0.0, -40.0)
        drawer.fillTriangle(a, b, c, Color.blue, g)
        drawer.drawLine(axisLength.toDouble(), 0.0, 0.0, axisLength.toDouble(), 0.0, 0.0, g)
    }

    fun render(camera: Camera, g: Graphics) {
        val lowX = Point3d(if (negative) -axisLength.toDouble() else 0.0, 0.0, 0.0)
        val highX = Point3d(axisLength.toDouble(), 0.0, 0.0)
        val lowY = Point3d(0.0, if (negative) -axisLength.toDouble() else 0.0, 0.0)
        val highY = Point3d(0.0, axisLength.toDouble(), 0.0)
        val lowZ = Point3d(0.0, 0.0, if (negative) -axisLength.toDouble() else 0.0)
        val highZ = Point3d(0.0, 0.0, axisLength.toDouble())
        g.color = color
        camera.drawLine(lowX, highX, g)
        camera.drawLine(lowY, highY, g)
        camera.drawLine(lowZ, highZ, g)
        /*var a = Point3d(0.0, 0.0, axisLength.toDouble())
        var b = Point3d(40.0, 0.0, (axisLength - 80).toDouble())
        var c = Point3d(-40.0, 0.0, (axisLength - 80).toDouble())
        camera.fillTriangle(a, b, c, Color.red, g)
        b = Point3d(0.0, 40.0, (axisLength - 80).toDouble())
        c = Point3d(0.0, -40.0, (axisLength - 80).toDouble())
        camera.fillTriangle(a, b, c, Color.red, g)
        a = Point3d(0.0, axisLength.toDouble(), 0.0)
        b = Point3d(0.0, (axisLength - 80).toDouble(), 40.0)
        c = Point3d(0.0, (axisLength - 80).toDouble(), -40.0)
        camera.fillTriangle(a, b, c, Color.green, g)
        b = Point3d(40.0, (axisLength - 80).toDouble(), 0.0)
        c = Point3d(-40.0, (axisLength - 80).toDouble(), 0.0)
        camera.fillTriangle(a, b, c, Color.green, g)
        a = Point3d(axisLength.toDouble(), 0.0, 0.0)
        b = Point3d((axisLength - 80).toDouble(), 40.0, 0.0)
        c = Point3d((axisLength - 80).toDouble(), -40.0, 0.0)
        camera.fillTriangle(a, b, c, Color.blue, g)
        b = Point3d((axisLength - 80).toDouble(), 0.0, 40.0)
        c = Point3d((axisLength - 80).toDouble(), 0.0, -40.0)
        camera.fillTriangle(a, b, c, Color.blue, g)*/
    }
}

private fun Camera.fillTriangle(a: Point3d, b: Point3d, c: Point3d, color: Color?, g: Graphics) {
    g.color = Color.black
    drawLine(a, b, g)
    drawLine(b, c, g)
    drawLine(c, a, g)
    g.color = color
    val a_ = project(a.positionVector).first
    val b_ = project(b.positionVector).first
    val c_ = project(c.positionVector).first
    val xValues = intArrayOf(a_.x.toInt(), b_.x.toInt(), c_.x.toInt())
    val yValues = intArrayOf(a_.y.toInt(), b_.y.toInt(), c_.y.toInt())
    g.fillPolygon(xValues, yValues, 3)
}

private fun Camera.drawLine(a: Point3d, b: Point3d, g: Graphics) {
    val pa = project(a.positionVector)
    val pb = project(b.positionVector)
    if (pa.second < 0 || pb.second < 0) return
    val a_ = pa.first
    val b_ = pb.first
    g.drawLine(a_.x.toInt(), a_.y.toInt(), b_.x.toInt(), b_.y.toInt())
}