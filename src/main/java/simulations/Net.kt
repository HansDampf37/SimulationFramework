package simulations

import algebra.CartesianCoordinateSystem
import algebra.Point3d
import algebra.Vec
import framework.Simulation
import framework.Vertex
import framework.physics.Seconds
import java.awt.Color
import java.awt.Graphics

@Suppress("unused")
class Net : Simulation("Net") {
    private val airResist = 0.999
    private val gravity = Vec(0.0, -100.0, 0.0)
    private lateinit var points: Array<Array<Point3d>>
    private lateinit var forces: Array<Array<Vec>>
    private val colors: Array<Array<Color>>
    var cart = CartesianCoordinateSystem(false, 1000, 1000.0, Color.black)

    init {
        reset()
        colors = Array(size) { Array(size) { Color(
            (163 + 40 * Math.random() - 20).toInt(),
            (153 + 40 * Math.random() - 20).toInt(),
            (239 + 20 * Math.random() - 10).toInt()
        ) } }
    }

    override fun tick(dt: Seconds) {
        input
        calcNetForces()
        airResist()
        movePoints()
    }

    override fun render() {
        points.forEach { it.forEach { point ->
            camera.renderSphere(
                Vertex(point.positionVector, Vec.ones * 255, Vec.zero),
                0.25f,
                null)
            }
        }
    }

    private fun movePoints() {
        for (x in 1 until points.size - 1) {
            for (y in 1 until points[x].size - 1) {
                points[x][y].add(forces[x][y])
                points[x][y].add(gravity)
            }
        }
    }

    private val input: Unit
        get() {
            if (keyManager.f) {
                moveEdge(Vec(0.0, 100.0, 0.0))
            }
            if (keyManager.g) {
                moveEdge(Vec(0.0, -100.0, 0.0))
            }
            if (keyManager.v) {
                moveEdge(Vec(100.0, 0.0, 0.0))
            }
            if (keyManager.b) {
                moveEdge(Vec(-100.0, 0.0, 0.0))
            }
        }

    private fun airResist() {
        for (x in 1 until points.size - 1) {
            for (y in 1 until points[x].size - 1) {
                forces[x][y].scaleInPlace(airResist)
            }
        }
    }

    private fun calcNetForces() {
        for (x in 1 until points.size - 1) {
            for (y in 1 until points[x].size - 1) {
                forces[x][y].addInPlace(
                    if (points[x][y]
                            .getConnectingVectorTo(points[x][y + 1]).length > 60
                    ) points[x][y]
                        .getConnectingVectorTo(points[x][y + 1]).scaleInPlace(0.1) else Vec(0.0, 0.0, 0.0)
                )
                forces[x][y].addInPlace(
                    if (points[x][y]
                            .getConnectingVectorTo(points[x + 1][y]).length > 60
                    ) points[x][y]
                        .getConnectingVectorTo(points[x + 1][y]).scaleInPlace(0.1) else Vec(0.0, 0.0, 0.0)
                )
                forces[x][y].addInPlace(
                    if (points[x][y]
                            .getConnectingVectorTo(points[x][y - 1]).length > 60
                    ) points[x][y]
                        .getConnectingVectorTo(points[x][y - 1]).scaleInPlace(0.1) else Vec(0.0, 0.0, 0.0)
                )
                forces[x][y].addInPlace(
                    if (points[x][y]
                            .getConnectingVectorTo(points[x - 1][y]).length > 60
                    ) points[x][y]
                        .getConnectingVectorTo(points[x - 1][y]).scaleInPlace(0.1) else Vec(0.0, 0.0, 0.0)
                )
                forces[x][y].scaleInPlace(0.99)
            }
        }
    }

    private fun moveEdge(delta: Vec) {
        for (i in points.indices) {
            points[0][i].add(delta)
            points[points.size - 1][i].add(delta)
        }
        for (i in 1 until points.size - 1) {
            points[i][0].add(delta)
            points[i][points.size - 1].add(delta)
        }
    }

    override fun reset() {
        points = Array(size) { x -> Array(size) { y ->
            Point3d(
                distBetweenPoints * (x + Math.random() - 0.5) - distBetweenPoints * size / 2,
                distBetweenPoints * (Math.random() - 0.5) - 50,
                distBetweenPoints * (y + Math.random() - 0.5) - distBetweenPoints * size / 2
            )
        } }
        forces = Array(size) { Array(size) { Vec(0.0, 0.0, 0.0) } }
    }

    companion object {
        private const val size = 30
        private const val distBetweenPoints = 300
    }
}