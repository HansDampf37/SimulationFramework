package framework.physics

import Conf
import algebra.Point3d
import algebra.Vec
import framework.Camera
import framework.Vertex
import framework.interfaces.*
import framework.interfaces.Mass
import framework.physics.collisions.BoundingBox
import kotlin.math.cos
import kotlin.math.sin

/**
 * Implementation of [Mass].
 */
open class PointMass(mass: Kg, x: Double, y: Double, z: Double, var radius: Double = 1.0) : Point3d(x, y, z), Mass {
    // Drawable
    override var outlineRasterization: Boolean = false
    override var color: Vec? = Conf.mass_color

    // Moveable
    override var velocity: Vec = Vec(0, 0, 0)
    override var acceleration: Vec = Vec(0, 0, 0)
    override var status = Status.Movable
    override var position: Vec
        get() = positionVector
        set(value) {
            x = value.x
            y = value.y
            z = value.z
        }

    // mass
    override val mass: Kg = mass

    init {
        require(mass != 0.0) { "Mass can't be equal to 0" }
    }

    constructor(mass: Double, positionVector: Vec, radius: Double = 1.0) : this(mass, positionVector.x, positionVector.y, positionVector.z, radius)

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

    fun getBoundingBox(): BoundingBox {
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
