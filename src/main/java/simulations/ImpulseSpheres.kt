package simulations

import algebra.Vec3
import framework.interfaces.Status
import framework.physics.ImpulseConnection
import framework.physics.PhysicsSimulation
import framework.physics.Sphere
import toVec
import java.awt.Color
import kotlin.math.PI

/**
 * We create a new simulation where a ball bounces up and down
 */
class ImpulseSpheres: PhysicsSimulation("Bounce") {
    private lateinit var balls: List<Sphere>
    private lateinit var anchors: List<Sphere>
    private lateinit var links: List<ImpulseConnection>
    init {
        reset()
        camera.focalLength = 10.0
        camera.x = 0.0
        camera.y = -37.0
        camera.z = 0.5
        camera.phi = PI
        camera.theta = PI / 2
        camera.focalLength = 10.0
        camera.zoom = 0.01
    }

    /**
     * Calc forces is invoked once per tick and enables us to apply gravity to the ball and invert its direction if needed.
     */
    override fun calcForces() = Unit

    /**
     * Reset the Ball
     */
    override fun setup() {
        balls = List(7) {Sphere(it.toDouble(), 0.0, 0.0, 0.5, 1.0)}
        anchors = List(7) {Sphere(it.toDouble(), 0.0, 4.0, 0.5, 1.0)}
        anchors.forEach { it.status = Status.Immovable }
        links = List(7) { ImpulseConnection(balls[it], anchors[it], 4.0, 1000.0) }
        balls[0].set(Vec3(-4, 0.0, 4.0))
        balls[0].color = Color.BLUE.toVec()
        balls[1].color = Color.RED.toVec()
        balls[2].color = Color.GREEN.toVec()
        balls[3].color = Color.YELLOW.toVec()
        balls[4].color = Color.ORANGE.toVec()
        balls[5].color = Color.MAGENTA.toVec()
        balls[6].color = Color.PINK.toVec()
        anchors.forEach { it.color = Color.GRAY.toVec() }
        balls.forEach { register(it) }
        anchors.forEach { register(it) }
        links.forEach { register(it) }
    }
}
fun main() {
    ImpulseSpheres().start()
}