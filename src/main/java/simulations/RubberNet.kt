package simulations

import algebra.Vec3
import framework.WatchDouble
import framework.WatchInt
import framework.interfaces.Status
import framework.physics.PhysicsSimulation
import framework.physics.Seconds
import framework.physics.Sphere
import framework.physics.SpringConnection
import toVec
import kotlin.math.PI

@Suppress("unused")
class RubberNet : PhysicsSimulation("Net") {
    private val airResist = 0.9
    private val points: MutableList<MutableList<Sphere>> = ArrayList()
    private val springConnections: MutableList<SpringConnection> = ArrayList()

    @WatchInt("Size", 1, 20)
    private var size = 10
        set(value) {
            field = value
            reset()
        }
    @WatchDouble("Connections resting distance", 0.0, 10.0)
    private var restingDist = 0.5
        set(value) {
            field = value
            for (connection in springConnections) connection.restingDistance = value
        }
    @WatchDouble("Connections initial distance", 0.0, 10.0)
    private var spawnDist = 1.0
        set(value) {
            field = value
            reset()
        }
    private val maxEnergy = 100.0 * size * size
    @WatchDouble("Connections spring constant", 1.0, 100.0)
    private var springConstant = 10.0
        set(value) {
            field = value
            for (connection in springConnections) connection.springConstant = value
        }

    override fun calcForces() = Unit

    init {
        reset()
        camera.x = 0.0
        camera.y = -50.0
        camera.z = 20.0
        camera.phi = PI
        camera.theta = PI / 2.5
        camera.zoom = 0.001
    }

    override fun render() {
        synchronized(points) {
            points.forEach {
                it.forEach { point ->
                    point.render(camera)
                }
            }
        }
        synchronized(springConnections) {
            springConnections.forEach { it.render(camera) }
        }
    }

    override fun listenForInput(dt: Seconds) {
        super.listenForInput(dt)
        if (keyManager.f) {
            moveEdge(Vec3(0.0, dt, 0.0))
        }
        if (keyManager.g) {
            moveEdge(Vec3(0.0, -dt, 0.0))
        }
        if (keyManager.v) {
            moveEdge(Vec3(dt, 0.0, 0.0))
        }
        if (keyManager.b) {
            moveEdge(Vec3(-dt, 0.0, 0.0))
        }
    }

    private fun moveEdge(delta: Vec3) {
        synchronized(points) {
            for (i in points.indices) {
                points[0][i].positionVector.addInPlace(delta)
                points[points.size - 1][i].positionVector.addInPlace(delta)
            }
            for (i in 1 until points.size - 1) {
                points[i][0].positionVector.addInPlace(delta)
                points[i][points.size - 1].positionVector.addInPlace(delta)
            }
        }
    }

    override fun reset() {
        synchronized(points) {
            points.clear()
            for (x in 0 until size) {
                points.add(ArrayList())
                for (y in 0 until size) {
                    val sphere = Sphere(
                        spawnDist * (x.toDouble() - size / 2),
                        spawnDist * (y.toDouble() - size / 2),
                        0.0,
                        0.1,
                        1.0
                    )
                    if (x == 0 || y == 0 || x == size - 1 || y == size - 1) sphere.status = Status.Immovable
                    sphere.color = Conf.colorScheme.smallObjectColor.toVec()
                    points[x].add(sphere)
                    register(sphere)
                }
            }
            synchronized(springConnections) {
                springConnections.clear()
                for (x in 1 until size - 1) {
                    for (y in 1 until size - 1) {
                        for (neighbor in neighbors(x, y)) {
                            val c = SpringConnection(points[x][y], neighbor, springConstant, restingDist, maxEnergy)
                            c.color = Conf.colorScheme.smallObjectColor.toVec()
                            springConnections.add(c)
                            register(c)
                        }
                    }
                }
            }
        }
    }

    private fun neighbors(x: Int, y: Int) = synchronized(points) {
        listOf(points[x - 1][y], points[x + 1][y], points[x][y - 1], points[x][y + 1])
    }
}

fun main() = RubberNet().start()