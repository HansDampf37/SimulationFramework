package physics

import algebra.Vec
import framework.Camera
import framework.Graphics3d
import framework.Vertex
import java.awt.Color
import java.awt.Graphics

open class Sphere(
    x: Double,
    y: Double,
    z: Double,
    val radius: Double, mass: Double): Mass(mass, x, y, z) {

    val color: Vec = Vec.random * 255

    fun testForCollision(other: Sphere): Boolean {
        return this.getDistanceTo(other) < this.radius + other.radius
    }

    override fun render(drawer: Graphics3d, g: Graphics) {
        drawer.drawDot(this, radius = radius, Color.white, g)
    }

    fun render(cam: Camera) {
        cam.renderCircle(Vertex(this.positionVector, color, Vec.zero), radius.toFloat())
    }
}
