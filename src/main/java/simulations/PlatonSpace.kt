package simulations

import algebra.CartesianCoordinateSystem
import algebra.Point3d
import algebra.Vec3BLablabla
import framework.Simulation
import framework.Vertex
import framework.physics.Seconds
import java.awt.Color
import java.awt.Graphics
import kotlin.math.pow

/**
 * Simulation that simulated points that are repelling each other yet can not leave the surface of a sphere.
 */
@Suppress("unused")
class PlatonSpace(private val amountOfPoint3ds: Int) : Simulation("Platon") {
    private lateinit var points: Array<Point3d>
    private lateinit var forces: Array<Vec3BLablabla>
    private val radius = 1000
    private var coordSys = CartesianCoordinateSystem(true, radius * 2, (radius / 10).toDouble(), Color(110, 106, 160))
    private var colorLines = Vec3BLablabla(200, 200, 200)
    private var colorPoints = Vec3BLablabla(163, 153, 239)

    init {
        reset()
    }

    override fun tick(dt: Seconds) {
        calcResultingForceOnPoint3d()
        movePoints()
        keepPointsInOrb()
    }

    override fun render() {
        TODO("Not yet implemented")
    }

    private fun drawForces(g: Graphics) {
        for (i in points.indices) {
            val force = forces[i]
            g.color = Color.ORANGE
            val shorten = 1 / force.length
            camera.renderLine(
                Vertex(points[i].positionVector, colorLines, Vec3BLablabla.zero),
                Vertex(points[i].positionVector + force * shorten, colorLines, Vec3BLablabla.zero)
            )
        }
    }

    private fun keepPointsInOrb() {
        for (i in points.indices) {
            if (points[i].positionVector.length > radius) {
                val positionVector = points[i].positionVector
                points[i].set(positionVector.scaleInPlace(radius / positionVector.length))
            }
        }
    }

    private fun movePoints() {
        for (i in points.indices) {
            points[i].add(forces[i])
        }
    }

    private fun calcResultingForceOnPoint3d() {
        for (i in points.indices) {
            val first = points[i]
            for (other in points) {
                if (first != other) {
                    val scalar: Double = 10000000 / first.getDistanceTo(other).pow(2.0)
                    forces[i].addInPlace(other.getDirectionTo(first).scaleInPlace(scalar))
                }
            }
        }
    }

    private fun drawPoints(g: Graphics) {
        for (point in points) {
            camera.renderSphere(Vertex(point.positionVector, colorPoints, Vec3BLablabla.zero), 0.25f)
        }
        var shortestDist = Int.MAX_VALUE.toDouble()
        for (i in points.indices) {
            for (j in i until points.size) {
                if (j != i) {
                    val dist = points[i].getDistanceTo(points[j])
                    if (dist < shortestDist) shortestDist = dist
                }
            }
        }
        for (i in points.indices) {
            for (j in i until points.size) {
                if (j != i) {
                    if (points[i].getDistanceTo(points[j]) < 1.3 * shortestDist) {
                        camera.renderLine(
                            Vertex(points[i].positionVector, colorLines, Vec3BLablabla.zero),
                            Vertex(points[j].positionVector, colorLines, Vec3BLablabla.zero)
                        )
                    }
                }
            }
        }
    }

    override fun reset() {
        points = Array(amountOfPoint3ds) {
            Point3d(
                2 * radius * Math.random() - radius,
                2 * radius * Math.random() - radius,
                2 * radius * Math.random() - radius
            )
        }
        forces = Array(amountOfPoint3ds) { Vec3BLablabla(0.0, 0.0, 0.0) }
        for (i in points.indices) points[i] = Point3d(
            2 * radius * Math.random() - radius,
            2 * radius * Math.random() - radius,
            2 * radius * Math.random() - radius
        )
        for (i in forces.indices) forces[i] = Vec3BLablabla(0.0, 0.0, 0.0)
    }
}