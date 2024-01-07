package simulations

import spacesimulation.MassSimulation
import spacesimulation.Simulator
import spacesimulation.algebra.Point3d
import spacesimulation.physics.Connection
import spacesimulation.physics.Mass
import java.awt.Graphics

class Cloth(simulator: Simulator, val size: Int): MassSimulation(simulator = simulator) {
    private lateinit var connections: MutableList<Connection>
    init {
        reset()
    }
    override fun render(g: Graphics) {
        masses.forEach{it.render(drawer, g)}
        connections.forEach{it.render(drawer, g)}
    }

    override fun calcForces(dtInSec: Double) {
        connections.forEach { it.tick(dtInSec) }
    }

    override fun buffer() = Unit

    override fun reset() {
        masses.clear()
        for (x in 0 until size) {
            for (y in 0 until size) {
                val edge = (x == 0) or (y == 0) or (x == size - 1) or (y == size - 1)
                addNewMass(Point3d(x.toDouble(), 0.0, y.toDouble()), !edge)
                masses.last().status = if (edge) Mass.Status.Immovable else Mass.Status.Movable
            }
        }
        connections = ArrayList()
        for (x in 0 until size) {
            for (y in 0 until size) {
                if (x + 1 < size) connections.add(Connection(masses[x * size + y], masses[(x + 1) * size + y], 1.1))
                if (y + 1 < size) connections.add(Connection(masses[x * size + y], masses[x * size + y + 1], 1.1))
            }
        }
    }
}