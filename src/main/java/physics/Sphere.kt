package physics

import Conf
import algebra.Vec
import framework.Camera
import framework.Vertex
import physics.collisions.BoundingBox
import physics.collisions.Collidable
import kotlin.math.cos
import kotlin.math.sin

/**
 * A Sphere is a round [Mass] that can be [collided][Collidable] with.
 */
open class Sphere(
    x: Double,
    y: Double,
    z: Double,
    var radius: Double, mass: Double
) : Mass(mass, x, y, z), Collidable {
    override fun render(camera: Camera) {
        camera.renderSphere(Vertex(this.positionVector, color ?: Vec.zero, Vec.zero), radius.toFloat(), this)
    }

    @Suppress("unused")
    fun renderStrip(cam: Camera) {
        cam.renderStrip(triangleStrip(cam), this)
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
                val angleCombinationsForVertex =
                    listOf(Pair(theta1, phi1), Pair(theta2, phi1), Pair(theta1, phi2), Pair(theta2, phi2))
                val tempVertices = Array(4) { i ->
                    val (theta, phi) = angleCombinationsForVertex[i]
                    val v1 = Vec(
                        x + radius * sin(theta) * cos(phi),
                        y + radius * sin(theta) * sin(phi),
                        z + radius * cos(theta)
                    )
                    val n1 = v1 - this.positionVector
                    val shadingFactor1 = maxOf(0.0, n1 * Vec(0.0, 0.0, 1.0))
                    Vertex(v1, (color ?: Vec.zero) * shadingFactor1, n1)
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

    override fun getBoundingBox(): BoundingBox {
        x = this.positionVector.x
        y = this.positionVector.y
        z = this.positionVector.z
        return BoundingBox(
            x - radius, x + radius,
            y - radius, y + radius,
            z - radius, z + radius
        )
    }
}
