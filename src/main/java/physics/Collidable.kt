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

    override fun render(cam: Camera, g: Graphics) {
        val (coords, distance) = cam.project(this.positionVector)
        if (distance < 0) return
        g.color = Color.WHITE
        val drawingRadius = radius * cam.focalLength / (distance * cam.zoom)
        g.fillOval(
            (coords.x - drawingRadius).toInt(),
            (coords.y - drawingRadius).toInt(),
            (2 * drawingRadius).toInt(),
            (2 * drawingRadius).toInt())
    }
}
