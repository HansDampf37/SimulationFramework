package simulations

import algebra.CartesianCoordinateSystem
import algebra.Point3d
import algebra.Vec3
import framework.Simulation
import framework.Vertex
import framework.physics.Seconds
import framework.physics.Sphere
import toVec
import java.awt.Color
import kotlin.math.pow

@Suppress("unused")
class Net : Simulation("Net") {
    private val airResist = 0.9
    private val gravity = Vec3(0.0, 0.0, -9.81)
    private lateinit var points: Array<Array<Vec3>>
    private lateinit var forces: Array<Array<Vec3>>

    init {
        reset()
        speed = 0.0
    }

    override fun tick(dt: Seconds) {
        input
        calcNetForces(dt)
        airResist(dt)
        movePoints(dt)
    }

    override fun render() {
        points.forEach {
            it.forEach { point ->
                camera.renderSphere(
                    Vertex(point, Conf.colorScheme.smallObjectColor.toVec(), Vec3.zero),
                    25f,
                    null
                )
            }
        }
        for (x in 1 until points.size - 1) {
            for (y in 1 until points[x].size - 1) {
                val point1 = points[x][y]
                val neighbor1 = points[x][y + 1]
                val neighbor2 = points[x][y - 1]
                val neighbor3 = points[x + 1][y]
                val neighbor4 = points[x - 1][y]
                for (neighbor in listOf(neighbor1, neighbor2, neighbor3, neighbor4)) {
                    camera.renderLine(
                        Vertex(point1, Conf.colorScheme.smallObjectColor.toVec(), Vec3.zero),
                        Vertex(neighbor, Conf.colorScheme.smallObjectColor.toVec(), Vec3.zero)
                    )
                }
            }
        }
    }

    private fun movePoints(dt: Seconds) {
        for (x in 1 until points.size - 1) {
            for (y in 1 until points[x].size - 1) {
                points[x][y].addInPlace(forces[x][y])
            }
        }
    }

    private val input: Unit
        get() {
            if (keyManager.f) {
                moveEdge(Vec3(0.0, 100.0, 0.0))
            }
            if (keyManager.g) {
                moveEdge(Vec3(0.0, -100.0, 0.0))
            }
            if (keyManager.v) {
                moveEdge(Vec3(100.0, 0.0, 0.0))
            }
            if (keyManager.b) {
                moveEdge(Vec3(-100.0, 0.0, 0.0))
            }
        }

    private fun airResist(dt: Seconds) {
        for (x in 1 until points.size - 1) {
            for (y in 1 until points[x].size - 1) {
                forces[x][y].scaleInPlace(airResist.pow(dt))
            }
        }
    }

    private fun calcNetForces(dt: Seconds) {
        for (x in 1 until points.size - 1) {
            for (y in 1 until points[x].size - 1) {
                forces[x][y].addInPlace(
                    if ((points[x][y + 1] - points[x][y]).length > 60) {
                        (points[x][y + 1] - points[x][y]).scaleInPlace(0.1) * dt
                    } else Vec3(0.0, 0.0, 0.0)
                )
                forces[x][y].addInPlace(
                    if ((points[x + 1][y] - points[x][y]).length > 60) {
                        (points[x + 1][y] - points[x][y]).scaleInPlace(0.1) * dt
                    } else Vec3(0.0, 0.0, 0.0)
                )
                forces[x][y].addInPlace(
                    if ((points[x][y - 1] - points[x][y]).length > 60) {
                        (points[x][y - 1] - points[x][y]).scaleInPlace(0.1) * dt
                    } else Vec3(0.0, 0.0, 0.0)
                )
                forces[x][y].addInPlace(
                    if ((points[x - 1][y] - points[x][y]).length > 60) {
                        (points[x - 1][y] - points[x][y]).scaleInPlace(0.1) * dt
                    } else Vec3(0.0, 0.0, 0.0)
                )
                forces[x][y].addInPlace(gravity * dt)
            }
        }
    }

    private fun moveEdge(delta: Vec3) {
        for (i in points.indices) {
            points[0][i].addInPlace(delta)
            points[points.size - 1][i].addInPlace(delta)
        }
        for (i in 1 until points.size - 1) {
            points[i][0].addInPlace(delta)
            points[i][points.size - 1].addInPlace(delta)
        }
    }

    override fun reset() {
        points = Array(size) { x ->
            Array(size) { y ->
                Vec3(
                    distBetweenPoints * (x.toDouble() - size / 2),
                    distBetweenPoints * (y.toDouble() - size / 2),
                    0.0,
                )
            }
        }
        forces = Array(size) { Array(size) { Vec3(0.0, 0.0, 0.0) } }
    }

    companion object {
        private const val size = 30
        private const val distBetweenPoints = 300
    }
}

fun main() = Net().start()