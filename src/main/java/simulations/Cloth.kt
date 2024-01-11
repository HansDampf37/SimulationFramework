package simulations

import spacesimulation.MassSimulation
import spacesimulation.Simulator
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
        masses.shuffle()
        connections.shuffle()
        connections.forEach { it.tick(dt) }
        sphere.tick(dt)
        masses.forEach {
            if (it != sphere) {
                if (sphere.testForCollision(it)) {
                    Collision.occur(sphere, it, 1.0)
                    val targetDistance = sphere.radius + it.radius
                    val overlap = targetDistance - sphere.getDistanceTo(it)
                    val massMovable = it.status == Mass.Status.Movable
                    val overlap1 = if (massMovable) sphere.mass / (it.mass + sphere.mass) * overlap else 0
                    val overlap2 = if (massMovable) it.mass / (it.mass + sphere.mass) * overlap else overlap
                    sphere.set(sphere + it.getDirectionTo(sphere) * overlap2)
                    it.set(it + sphere.getDirectionTo(it) * overlap1)
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
            for (z in 0 until size) {
                val isOnEdge = (x == 0) or (z == 0) or (x == size - 1) or (z == size - 1)
                val c = Collidable(x.toDouble() - size / 2.0 + 0.5, 0.0, z.toDouble() - size / 2.0 + 0.5, .25, 1.0)
                addNewMass(c, !isOnEdge)
                masses.last().status = if (isOnEdge) Mass.Status.Immovable else Mass.Status.Movable
            }
        }
        connections = ArrayList()
        for (x in 0 until size) {
            for (y in 0 until size) {
                if (x + 1 < size) connections.add(ImpulseConnection(masses[x * size + y], masses[(x + 1) * size + y], 1.1, 1000.0))
                if (y + 1 < size) connections.add(ImpulseConnection(masses[x * size + y], masses[x * size + y + 1], 1.1, 1000.0))
            }
        }

        sphere = Sphere(0.0, 10.0, 0.0, 2.0, 20.0)
        addNewMass(sphere, true)
    }
}