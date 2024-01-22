package physics

import algebra.Vec
import framework.Camera
import framework.Graphics3d
import framework.Vertex
import java.awt.Color
import java.awt.Graphics
import java.util.ArrayList
import kotlin.math.cos
import kotlin.math.sin

open class Sphere(
    x: Double,
    y: Double,
    z: Double,
    var radius: Double, mass: Double): Mass(mass, x, y, z) {

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

    fun renderStrip(cam: Camera) {
        cam.renderStrip(triangleStrip(cam))
    }

    private fun triangleStrip(camera: Camera): List<Vertex> {
        val vertices = ArrayList<Vertex>()
        val numDivisions = (radius * camera.focalLength / (camera.zoom * getDistanceTo(camera)) / 1).toInt()
        for (lat in 0 until numDivisions) {
            for (lon in 0 until numDivisions * 2) {
                val theta1 = (lat * Math.PI / numDivisions).toFloat()
                val theta2 = ((lat + 1) * Math.PI / numDivisions).toFloat()
                val phi1 = (lon * 2 * Math.PI / (numDivisions * 2)).toFloat()
                val phi2 = ((lon + 1) * 2 * Math.PI / (numDivisions * 2)).toFloat()
                val angleCombinationsForVertex = listOf(Pair(theta1, phi1), Pair(theta2, phi1), Pair(theta1, phi2), Pair(theta2, phi2))
                val tempVertices = Array(4) {i ->
                    val (theta, phi) = angleCombinationsForVertex[i]
                    val v1 = Vec(
                        x + radius * sin(theta) * cos(phi),
                        y + radius * sin(theta) * sin(phi),
                        z + radius * cos(theta)
                    )
                    val n1 = v1 - this.positionVector
                    val shadingFactor1 = maxOf(0.0, n1 * Vec(0.0, 0.0, 1.0))
                    Vertex(v1, color * shadingFactor1, n1)
                }

                vertices.add(tempVertices[0])
                vertices.add(tempVertices[1])
                vertices.add(tempVertices[2])

                vertices.add(tempVertices[1])
                vertices.add(tempVertices[3])
                vertices.add(tempVertices[2])
            }
        }
        return vertices
    }
}
