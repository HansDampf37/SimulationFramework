package simulations

import algebra.CartesianCoordinateSystem
import algebra.Point3d
import algebra.Vec3
import framework.Simulation
import framework.physics.Seconds
import java.awt.Color

@Suppress("unused")
class Pendulum(private val amountOfPoints: Int) : Simulation("Pendulum") {
    private var cart = CartesianCoordinateSystem(true, 500, 500.0, Color.black)
    private val maxRopeSegmentLength = 10000
    private val gravity = Vec3(0.0, -9.81, 0.0)
    private lateinit var points: Array<Point3d>
    private lateinit var velocities: Array<Vec3>
    private lateinit var forces: Array<Vec3>
    private val airResist = 0.999

    init {
        reset()
    }

    override fun tick(dt: Seconds) {
        input
        calcForcesOnPoints()
        airResist()
        movePoints()
    }

    override fun render() {
        TODO("Not yet implemented")
    }

    private val input: Unit
        get() {
            if (keyManager.up) forces[0] = Vec3(10.0, 0.0, 0.0)
            if (keyManager.down) forces[0] = Vec3(-10.0, 0.0, 0.0)
            if (keyManager.left) forces[0] = Vec3(0.0, 0.0, 10.0)
            if (keyManager.right) forces[0] = Vec3(0.0, 0.0, -10.0)
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
                    throw NotImplementedError()
                }
            }
            forces[i].addInPlace(gravity)
        }
    }

    override fun reset() {
        points = Array(amountOfPoints) { i -> Point3d(0.0, -i * maxRopeSegmentLength * 0.5, 0.0) }
        velocities = Array(amountOfPoints) { _ -> Vec3(0.0, 0.0, 0.0) }
        forces = Array(amountOfPoints) { _ -> Vec3(0.0, 0.0, 0.0) }
        for (i in points.indices) {
            points[i] = Point3d(0.0, -i * maxRopeSegmentLength * 0.5, 0.0)
            forces[i] = Vec3(0.0, 0.0, 0.0)
            velocities[i] = Vec3(0.0, 0.0, 0.0)
        }
    }
}