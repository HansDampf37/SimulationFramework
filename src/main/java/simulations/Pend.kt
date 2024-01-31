package simulations

import algebra.Point3d
import algebra.Vec
import framework.MassSimulation
import framework.WatchDouble
import framework.WatchInt
import framework.interfaces.Status
import physics.*
import java.awt.Graphics
import java.lang.Thread.sleep
import kotlin.math.PI

@Suppress("unused")
class Pend(
    amountOfPoints: Int,
    @WatchDouble("Length", 1.0, 20.0)
    private val length: Double
) : MassSimulation<Mass>("String") {
    @WatchInt("Segments", 1, 50)
    private var amountOfPoints: Int = amountOfPoints
        set(value) {
            field = value
            synchronized(connections) {
                connections.subList(value - 1, connections.size).forEach { it.broken = true }
            }
            synchronized(masses) {
                repeat(value - masses.size) {
                    val pos = masses.last().positionVector - Vec(0, 0, maxRopeSegmentLength * 0.8)
                    val mass = Sphere(pos.x, pos.y, pos.z, 0.25, 1.0)
                    connections.add(ImpulseConnection(masses.last(), mass, maxRopeSegmentLength, 30.0))
                    addNewMass(mass)
                }
            }
            Thread {
                sleep(1000)
                synchronized(masses) {
                    for (i in (value until masses.size).reversed()) masses.removeAt(i)
                }
            }.start()
        }
    private val maxRopeSegmentLength: Double
        get() = length / amountOfPoints
    private val connections: MutableList<Connection> = ArrayList()

    init {
        reset()
    }

    override fun calcForces(dt: Seconds) {
        input()
        synchronized(connections) {
            connections.forEach { it.tick(dt) }
        }
    }

    fun correct() {
        synchronized(masses) {
            synchronized(connections) {
                masses.forEachIndexed { i, mass ->
                    if (i != 0) {
                        if (connections[i - 1].broken) return
                        if (mass.getDistanceTo(masses[i - 1]) >= maxRopeSegmentLength) {
                            mass.set(masses[i - 1].positionVector + masses[i - 1].getDirectionTo(mass) * maxRopeSegmentLength)
                        }
                    }
                }
            }
        }
    }

    override fun render() {
        synchronized(masses) { for (m in masses) m.render(camera) }
        synchronized(connections) { connections.filter { !it.broken }.forEach { it.render(camera) } }
    }

    private fun input() {
        if (keyManager.f) masses[0].accelerate(Vec(40.0, 0.0, 0.0))
        if (keyManager.g) masses[0].accelerate(Vec(-40.0, 0.0, 0.0))
        if (keyManager.v) masses[0].accelerate(Vec(0.0, 20.0, 0.0))
        if (keyManager.b) masses[0].accelerate(Vec(0.0, -20.0, 0.0))
        if (keyManager.up) masses[1].applyForce(Vec(10.0, 0.0, 0.0))
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
            masses.clear()
            for (i in 0 until amountOfPoints) {
                val pos = Point3d(-i * maxRopeSegmentLength, 0.0, 0.0)
                val mass = Sphere(pos.x, pos.y, pos.z, 0.025, 1.0)
                if (i == 0) mass.status = Status.Immovable
                addNewMass(mass)
            }
        }
        masses[0].status = Status.Immovable
        synchronized(connections) {
            connections.clear()
            for (i in 0 until amountOfPoints - 1) {
                connections.add(ImpulseConnection(masses[i], masses[i + 1], maxRopeSegmentLength, 30.0))
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