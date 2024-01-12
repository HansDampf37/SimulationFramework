package algebra

import framework.Graphics3d
import java.awt.Color
import java.awt.Graphics

class CartesianCoordinateSystem(
    private val negative: Boolean,
    private val axisLength: Int,
    private val scaleSize: Double,
    private val color: Color
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
}