package physics

import framework.Camera
import framework.Simulateable
import framework.Graphics3d
import java.awt.Color
import java.awt.Graphics

open class Collidable(
    x: Double,
    y: Double,
    z: Double,
    val radius: Double, mass: Double): Mass(mass, x, y, z) {
    fun testForCollision(other: Collidable): Boolean {
        return this.getDistanceTo(other) < this.radius + other.radius
    }

    override fun render(drawer: Graphics3d, g: Graphics) {
        drawer.drawDot(this, radius = radius, Color.white, g)
    }
}

class Sphere(
    x: Double,
    y: Double,
    z: Double,
    radius: Double, mass: Double): Collidable(x, y, z, radius, mass), Simulateable {
    override fun render(drawer: Graphics3d, g: Graphics) {
        drawer.drawDot(this, radius = radius, Color.red, g)
    }

    override fun render(cam: Camera, g: Graphics) {
        val (coords, distance) = cam.project(this.positionVector)
        g.color = Color.WHITE
        g.fillOval(
            (coords.x - radius / cam.zoomX).toInt(),
            (coords.y - radius / cam.zoomY).toInt(),
            (2 * radius / cam.zoomX).toInt(),
            (2 * radius / cam.zoomY).toInt())
    }
}