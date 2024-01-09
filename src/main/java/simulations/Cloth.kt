package simulations

import spacesimulation.KeyManager
import spacesimulation.MassSimulation
import spacesimulation.Simulator
import spacesimulation.algebra.Point3d
import spacesimulation.algebra.Vec
import spacesimulation.physics.*
import java.awt.Graphics

class Cloth(simulator: Simulator, private val size: Int): MassSimulation<Collidable>(simulator = simulator) {
    private lateinit var connections: MutableList<Connection>
    private var sphere = Sphere(0.0, 0.0, 0.0, 10.0, 10.0)
    init {
        reset()
        drawer.setZoom(30.0)
        drawer.setCameraAngleHorizontal(0.2)
    }
    override fun render(g: Graphics) {
        masses.forEach{it.render(drawer, g)}
        connections.forEach{it.render(drawer, g)}
        sphere.render(drawer, g)
    }

    override fun calcForces(dt: Seconds) {
        input()
        connections.forEach { it.tick(dt) }
        sphere.tick(dt)
        masses.forEach {
            if (it != sphere) {
                if (sphere.testForCollision(it)) {
                    Collision.occur(sphere, it)
                }
            }
        }
    }

    private fun input() {
        if (keyManager.up) sphere.accelerate(Vec(40.0, 0.0, 0.0))
        if (keyManager.down) sphere.accelerate(Vec(-40.0, 0.0, 0.0))
        if (keyManager.left) sphere.accelerate(Vec(0.0, 0.0, 40.0))
        if (keyManager.right) sphere.accelerate(Vec(0.0, 0.0, -40.0))
        if (keyManager.shift) sphere.accelerate(Vec(0.0, 40.0, 0.0))
        if (keyManager.space) sphere.accelerate(Vec(0.0, -40.0, 0.0))
    }

    override fun reset() {
        masses.clear()
        for (x in 0 until size) {
            for (y in 0 until size) {
                val edge = (x == 0) or (y == 0) or (x == size - 1) or (y == size - 1)
                val c = Collidable(x.toDouble() - size/2, 0.0, y.toDouble() - size / 2, 1.0, 1.0)
                addNewMass(c, !edge)
                masses.last().status = if (edge) Mass.Status.Immovable else Mass.Status.Movable
            }
        }
        connections = ArrayList()
        for (x in 0 until size) {
            for (y in 0 until size) {
                if (x + 1 < size) connections.add(ImpulseConnection(masses[x * size + y], masses[(x + 1) * size + y], 1.1, 10.0))
                if (y + 1 < size) connections.add(ImpulseConnection(masses[x * size + y], masses[x * size + y + 1], 1.1, 10.0))
            }
        }

        sphere = Sphere(0.0, 10.0, 0.0, 3.0, 100.0)
        addNewMass(sphere, true)
    }
}