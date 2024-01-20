package simulations

import framework.MassSimulation
import algebra.Point3d
import algebra.Vec
import physics.Connection
import physics.ImpulseConnection
import physics.Mass
import physics.Seconds
import java.awt.Graphics

class Pend(private val amountOfPoints: Int, length: Double) : MassSimulation<Mass>("String") {
    private val maxRopeSegmentLength: Double
    private val connections: MutableList<Connection> = ArrayList()

    init {
        maxRopeSegmentLength = length / amountOfPoints
        reset()
        drawer.setZoom(30.0)
        drawer.setCameraAngleHorizontal(0.2)
    }

    override fun calcForces(dt: Seconds) {
        input()
        connections.forEach { it.tick(dt) }
    }

    override fun render() {
        TODO("Not yet implemented")
    }

    private fun input() {
            if (keyManager.f) masses[0].accelerate(Vec(40.0, 0.0, 0.0))
            if (keyManager.g) masses[0].accelerate(Vec(-40.0, 0.0, 0.0))
            if (keyManager.v) masses[0].accelerate(Vec(0.0, 0.0, 20.0))
            if (keyManager.b) masses[0].accelerate(Vec(0.0, 0.0, -20.0))
            if (keyManager.up) masses[1].applyForce(Vec(10.0, 0.0, 0.0))
        }

    fun render(g: Graphics) {
        for (m in masses) m.render(drawer, g)
        for (c in connections) c.render(drawer, g)
        for (i in masses.indices) {
            g.drawString(masses[i].toString(), 10, 100 + i * 20)
        }
    }

    override fun reset() {
        masses.clear()
        for (i in 0 until amountOfPoints) {
            val pos = Point3d(0.0, -i * maxRopeSegmentLength * 0.7, 0.0)
            val mass = Mass(1.0, pos)
            addNewMass(mass, i != 0)
        }
        masses[0].status = Mass.Status.Immovable
        connections.clear()
        for (i in 0 until amountOfPoints - 1) {
            connections.add(ImpulseConnection(masses[i], masses[i + 1], maxRopeSegmentLength, 30.0))
        }
    }
}