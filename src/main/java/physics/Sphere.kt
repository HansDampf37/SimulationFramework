package physics

import algebra.Vec
import algebra.Vec2
import framework.*
import java.awt.Color
import java.awt.Graphics

open class Sphere(
    x: Double,
    y: Double,
    z: Double,
    val radius: Double, mass: Double): Mass(mass, x, y, z), Primitive {
    fun testForCollision(other: Sphere): Boolean {
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

    override fun getVertices(): Array<Vertex> {
        return arrayOf(Vertex(positionVector, Vec.ones * 255, Vec.zero))
    }

    override fun boundingBox(): BoundingBox {
        TODO("Not yet implemented")
    }

    override fun interpolateDepthColorNormal(pixel: Vec2): InterpolationResult {
        TODO("Not yet implemented")
    }
}
