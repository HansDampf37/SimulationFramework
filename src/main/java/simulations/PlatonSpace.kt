package simulations

import framework.Simulation
import framework.Simulator
import algebra.CartesianCoordinateSystem
import algebra.Point3d
import algebra.Vec
import physics.Seconds
import java.awt.Color
import java.awt.Graphics
import kotlin.math.pow

/**
 * Simulation that simulated points that are repelling each other yet can not leave the surface of a sphere.
 */
@SuppressWarnings("unused")
class PlatonSpace(private val amountOfPoint3ds: Int, simulator: Simulator?) : Simulation(simulator!!) {
    private lateinit var points: Array<Point3d>
    private lateinit var forces: Array<Vec>
    private val radius = 1000
    private var coordSys = CartesianCoordinateSystem(true, radius * 2, (radius / 10).toDouble(), Color(110, 106, 160))
    private var colorLines = Color(200, 200, 200)
    private var colorPoints = Color(163, 153, 239)

    init {
        reset()
    }

    override fun tick(dt: Seconds) {
        calcResultingForceOnPoint3d()
        movePoints()
        keepPointsInOrb()
        drawer.setWindowHeightAndWidth(simulator.width, simulator.height)
    }

    override fun render(g: Graphics) {
        coordSys.render(drawer, g)
        drawPoints(g)
        if (simulator.keyManager.f) drawForces(g)
    }

    private fun drawForces(g: Graphics) {
        for (i in points.indices) {
            val force = forces[i]
            g.color = Color.ORANGE
            val shorten = 1 / force.length
            drawer.drawLine(
                points[i].x,
                points[i].y,
                points[i].z,
                points[i].x + force.x * shorten,
                points[i].y + force.y * shorten,
                points[i].z + force.z * shorten,
                g
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
            drawer.drawDot(point, 0.25, colorPoints, g)
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
                        g.color = colorLines
                        drawer.drawLine(points[i], points[j], g)
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
        forces = Array(amountOfPoint3ds) { Vec(0.0, 0.0, 0.0) }
        for (i in points.indices) points[i] = Point3d(
            2 * radius * Math.random() - radius,
            2 * radius * Math.random() - radius,
            2 * radius * Math.random() - radius
        )
        for (i in forces.indices) forces[i] = Vec(0.0, 0.0, 0.0)
    }
}