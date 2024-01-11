package simulations

import spacesimulation.Simulation
import spacesimulation.Simulator
import spacesimulation.algebra.CartesianCoordinateSystem
import spacesimulation.algebra.Point3d
import spacesimulation.algebra.Vec
import spacesimulation.physics.Seconds
import java.awt.Color
import java.awt.Graphics

class Pendulum(val amountOfPoints: Int, sim: Simulator) : Simulation(sim) {
    var cart = CartesianCoordinateSystem(true, 500, 500.0, Color.black)
    private val maxRopeSegmentLength = 10000
    private val gravity = Vec(0.0, -9.81, 0.0)
    private lateinit var points: Array<Point3d>
    private lateinit var velocities: Array<Vec>
    private lateinit var forces: Array<Vec>
    private val airResist = 0.999

    init {
        reset()
        drawer.setZoom(0.03)
    }

    override fun tick(dt: Seconds) {
        input
        calcForcesOnPoints()
        airResist()
        movePoints()
    }

    private val input: Unit
        get() {
            if (keyManager.up) forces[0] = Vec(10.0, 0.0, 0.0)
            if (keyManager.down) forces[0] = Vec(-10.0, 0.0, 0.0)
            if (keyManager.left) forces[0] = Vec(0.0, 0.0, 10.0)
            if (keyManager.right) forces[0] = Vec(0.0, 0.0, -10.0)
        }

    private fun airResist() {
        for (i in forces.indices) {
            forces[i].scaleInPlace(airResist)
        }
    }

    private fun movePoints() {
        points[0].add(forces[0])
        for (i in 1 until points.size) {
            points[i].add(forces[i])
            if (points[i - 1].getConnectingVectorTo(points[i]).length > maxRopeSegmentLength) {
                val posVec = points[i - 1].getConnectingVectorTo(points[i])
                val scalar = maxRopeSegmentLength / posVec.length
                posVec.scaleInPlace(scalar)
                points[i].set(points[i - 1].positionVector.addInPlace(posVec))
            }
        }
    }

    private fun calcForcesOnPoints() {
        for (i in 1 until points.size) {
            if (points[i].getDistanceTo(points[i - 1]) >= maxRopeSegmentLength) {
                val ropeDir = points[i].getDirectionTo(points[i - 1])
                if (i == 1) {
                    val forceInRopeDir = forces[1].projectOnto(ropeDir)
                    forceInRopeDir.scaleInPlace(-2.0)
                    forces[1].addInPlace(forceInRopeDir)
                    forces[1].addInPlace(forces[0].projectOnto(forceInRopeDir))
                } else {
                }
            }
            forces[i].addInPlace(gravity)
        }
    }

    override fun render(g: Graphics) {
        cart.render(drawer, g)
        for (i in 0 until points.size - 1) {
            drawer.drawDot(points[i], 0.25, Color.lightGray, g)
            drawer.drawLine(points[i], points[i + 1], g)
        }
        drawer.drawDot(points[points.size - 1], 0.25, Color.lightGray, g)
    }

    override fun reset() {
        points = Array(amountOfPoints) { i -> Point3d(0.0, -i * maxRopeSegmentLength * 0.5, 0.0) }
        velocities = Array(amountOfPoints) { _ -> Vec(0.0, 0.0, 0.0) }
        forces = Array(amountOfPoints) { _ -> Vec(0.0, 0.0, 0.0) }
        for (i in points.indices) {
            points[i] = Point3d(0.0, -i * maxRopeSegmentLength * 0.5, 0.0)
            forces[i] = Vec(0.0, 0.0, 0.0)
            velocities[i] = Vec(0.0, 0.0, 0.0)
        }
    }
}