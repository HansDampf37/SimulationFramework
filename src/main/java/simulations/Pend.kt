package simulations

import Conf
import algebra.Point3d
import framework.WatchDouble
import framework.WatchInt
import framework.interfaces.Status
import framework.physics.ImpulseConnection
import framework.physics.PhysicsSimulation
import framework.physics.PointMass
import toVec
import kotlin.math.PI

@Suppress("unused")
class Pend(amountOfPoints: Int, length: Double) : PhysicsSimulation("String") {
    private val radius = 0.025
    private val links = ArrayList<ImpulseConnection>()
    private val masses = ArrayList<PointMass>()
    private var maxEnergy = 30.0
        set(value) = links.forEach { it.maxEnergy = value }
    private val maxRopeSegmentLength: Double
        get() = length / amountOfPoints
    @WatchDouble("Rope-Length", 1.0, 3.0)
    private var length: Double = length
        set(value) {
            field = value
            synchronized(links) {
                links.forEach { it.maxDistance = maxRopeSegmentLength }
            }
        }
    @WatchInt("Segments", 1, 100)
    private var amountOfPoints: Int = amountOfPoints
        set(value) {
            synchronized(links) {
                synchronized(masses) {
                    val delta = amountOfPoints - value
                    if (delta > 0) {
                        // field > value -> remove delta masses from the rope
                        repeat(delta) {
                            val removedMass = masses.removeLast()
                            val lastMass = masses.last()
                            val toBeRemoved = links.filter { it.isConnectedTo(removedMass) && it.isConnectedTo(lastMass) }
                            links.removeAll(toBeRemoved.toSet())
                            toBeRemoved.forEach { unregister(it) }
                        }
                    } else if (delta < 0) {
                        repeat(-delta) {
                            val lastMass = masses.last()
                            val mass = PointMass(
                                lastMass.mass,
                                lastMass.position.x,
                                lastMass.position.y,
                                lastMass.position.z - maxRopeSegmentLength * 0.8,
                                radius,
                            )
                            mass.color = Conf.colorScheme.smallObjectColor.toVec()
                            masses.add(mass)
                            register(mass)
                            val link = ImpulseConnection(lastMass, mass, maxRopeSegmentLength, maxEnergy)
                            links.add(link)
                            register(link)
                        }
                    }
                }
                links.forEach { it.maxDistance = maxRopeSegmentLength }
            }
            field = value
        }

    init {
        reset()
    }

    override fun render() {
        synchronized(masses) { for (m in masses) m.render(camera) }
        synchronized(links) { links.filter { !it.broken }.forEach { it.render(camera) } }
    }

    override fun calcForces() = Unit

    override fun reset() {
        super.reset()
        synchronized(masses) {
            masses.clear()
            for (i in 0 until amountOfPoints) {
                val pos = Point3d(-i * maxRopeSegmentLength, 0.0, 0.0)
                val mass = PointMass(1.0, pos.x, pos.y, pos.z, radius)
                if (i == 0) mass.status = Status.Immovable
                mass.color = Conf.colorScheme.smallObjectColor.toVec()
                masses.add(mass)
                register(mass)
            }
        }
        synchronized(links) {
            links.clear()
            for (i in 0 until amountOfPoints - 1) {
                val link = ImpulseConnection(masses[i], masses[i + 1], maxRopeSegmentLength, maxEnergy)
                links.add(link)
                register(link)
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

fun main() {
    Pend(10, 2.0).start()
}