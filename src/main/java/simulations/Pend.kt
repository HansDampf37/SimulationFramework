package simulations

import algebra.Point3d
import physics.PhysicsSimulation
import framework.WatchDouble
import framework.WatchInt
import framework.interfaces.Status
import physics.ImpulseConnection
import physics.Mass
import physics.Seconds
import physics.Sphere
import java.awt.Graphics
import kotlin.math.PI

@Suppress("unused")
class Pend(
    amountOfPoints: Int,
    length: Double,
) : PhysicsSimulation("String") {
    private val radius = 0.025
    private var maxEnergy = 30.0
        set(value) = connections.forEach { it.maxEnergy = value }
    private val maxRopeSegmentLength: Double
        get() = length / amountOfPoints
    @WatchDouble("Rope-Length", 1.0, 3.0)
    private var length: Double = length
        set(value) {
            field = value
            synchronized(connections) {
                connections.forEach { it.maxDistance = maxRopeSegmentLength }
            }
        }
    @WatchInt("Segments", 1, 100)
    private var amountOfPoints: Int = amountOfPoints
        set(value) {
            synchronized(connections) {
                synchronized(masses) {
                    val delta = amountOfPoints - value
                    if (delta > 0) {
                        // field > value -> remove delta masses from the rope
                        repeat(delta) {
                            val removedMass = moveables.removeLast() as Mass
                            val lastMass = moveables.last() as Mass
                            connections.removeAll { it.isConnectedTo(removedMass) && it.isConnectedTo(lastMass) }
                        }
                    } else if (delta < 0) {
                        repeat(-delta) {
                            val lastMass = masses.last()
                            val sphere = Sphere(lastMass.x, lastMass.y, lastMass.z - maxRopeSegmentLength * 0.8,
                                radius, lastMass.mass)
                            connections.add(ImpulseConnection(lastMass, sphere, maxRopeSegmentLength, maxEnergy))
                            add(sphere)
                        }
                    }
                }
                connections.forEach { it.maxDistance = maxRopeSegmentLength }
            }
            field = value
        }

    init {
        reset()
    }

    override fun calcForces(dt: Seconds) {
        synchronized(connections) {
            connections.forEach { it.tick(dt) }
            connections.removeAll { it.broken }
        }
    }

    override fun render() {
        synchronized(masses) { for (m in masses) m.render(camera) }
        synchronized(connections) { connections.filter { !it.broken }.forEach { it.render(camera) } }
    }

    fun render(g: Graphics) {
        for (m in masses) m.render(camera)
        for (c in connections) c.render(camera)
        for (i in masses.indices) {
            g.drawString(masses[i].toString(), 10, 100 + i * 20)
        }
    }

    override fun reset() {
        synchronized(masses) {
            moveables.clear()
            for (i in 0 until amountOfPoints) {
                val pos = Point3d(-i * maxRopeSegmentLength, 0.0, 0.0)
                val mass = Sphere(pos.x, pos.y, pos.z, radius, 1.0)
                if (i == 0) mass.status = Status.Immovable
                add(mass)
            }
        }
        masses[0].status = Status.Immovable
        synchronized(connections) {
            connections.clear()
            for (i in 0 until amountOfPoints - 1) {
                connections.add(ImpulseConnection(masses[i], masses[i + 1], maxRopeSegmentLength, maxEnergy))
            }
        }
        camera.focalLength = 10.0
        camera.x = 0.0
        camera.y = -25.0
        camera.z = -length / 2
        camera.theta = PI / 2
        camera.phi = PI
        camera.focalLength = 10.0
        camera.zoom = 0.001
    }
}