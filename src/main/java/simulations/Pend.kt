package simulations

import spacesimulation.MassSimulation
import spacesimulation.Simulator
import spacesimulation.algebra.Point3d
import spacesimulation.algebra.Vec
import spacesimulation.physics.Connection
import spacesimulation.physics.Mass
import java.awt.Graphics

class Pend(private val amountOfPoints: Int, sim: Simulator, length: Double) :
    MassSimulation(1.0, Vec(0.0, -9.81, -0.0), sim) {
    private val maxRopeSegmentLength: Double
    private val connections: MutableList<Connection> = ArrayList()

    init {
        maxRopeSegmentLength = length / amountOfPoints
        reset()
        drawer.setZoom(30.0)
        drawer.setCameraAngleHorizontal(0.2)
    }

    override fun buffer() {
        for (i in 1 until masses.size) {
            if (masses[i - 1].getConnectingVectorTo(masses[i]).length > maxRopeSegmentLength) {
                val posVec = masses[i - 1].getConnectingVectorTo(masses[i])
                val scalar = maxRopeSegmentLength / posVec.length
                posVec.scale(scalar)
                masses[i].set(masses[i - 1].positionVector.add(posVec))
            }
        }
    }

    override fun calcForces(dtInSec: Double) {
        input()
        connections.forEach { it.tick(dtInSec) }
    }

    private fun input() {
            if (keyManager.f) masses[0].accelerate(Vec(10.0, 0.0, 0.0))
            if (keyManager.g) masses[0].accelerate(Vec(-10.0, 0.0, 0.0))
            if (keyManager.v) masses[0].accelerate(Vec(0.0, 0.0, 10.0))
            if (keyManager.b) masses[0].accelerate(Vec(0.0, 0.0, -10.0))
            if (keyManager.up) masses[1].applyForce(Vec(10.0, 0.0, 0.0))
        }

    override fun render(g: Graphics) {
        for (m in masses) m.render(drawer, g)
        for (c in connections) c.render(drawer, g)
        for (i in masses.indices) {
            g.drawString(masses[i].toString(), 10, 100 + i * 20)
        }
    }

    override fun reset() {
        masses.clear()
        for (i in 0 until amountOfPoints) {
            addNewMass(Point3d(0.0, -i * maxRopeSegmentLength * 0.7, -i * maxRopeSegmentLength * 0.7), i != 0)
        }
        masses[0].status = Mass.Status.Immovable
        connections.clear()
        for (i in 0 until amountOfPoints - 1) {
            connections.add(Connection(masses[i], masses[i + 1], maxRopeSegmentLength))
        }
    }
}