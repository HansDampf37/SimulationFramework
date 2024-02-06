package simulations

import algebra.Vec
import physics.PhysicsSimulation
import physics.Plane
import physics.Sphere
import toVec
import java.awt.Color
import kotlin.math.PI

/**
 * We create a new simulation where a ball bounces up and down
 */
class BouncingBall: PhysicsSimulation("Bounce") {
    private lateinit var balls: List<Sphere>
    private lateinit var walls: List<Plane>
    private lateinit var floor: Plane
    init {
        reset()
        camera.focalLength = 10.0
        camera.x = 0.0
        camera.y = -37.0
        camera.z = 11.0
        camera.phi = PI
        camera.theta = PI / 2
        camera.focalLength = 10.0
        camera.zoom = 0.01
    }

    /**
     * Calc forces is invoked once per tick and enables us to apply gravity to the ball and invert its direction if needed.
     */
    override fun calcForces() {
        for (ball in balls) {
            if (ball.position.z < ball.radius && ball.velocity.z < 0) ball.velocity.z = -ball.velocity.z
            if ((ball.position.x - ball.radius < -10 && ball.velocity.x < 0) ||
                (ball.position.x + ball.radius > 10 && ball.velocity.x > 0) ) ball.velocity.x = -ball.velocity.x
            if ((ball.position.y - ball.radius < -10 && ball.velocity.y < 0) ||
                (ball.position.y + ball.radius > 10 && ball.velocity.y > 0) ) ball.velocity.y = -ball.velocity.y
            applyGravity(listOf(ball))
        }
    }

    /**
     * Reset the Ball
     */
    override fun reset() {
        super.reset()
        floor = Plane(listOf(Vec(-10, -10, 0), Vec(-10, 10, 0), Vec(10, -10, 0), Vec(10, 10, 0)))
        walls = listOf(Plane(listOf(Vec(10, -10, 0), Vec(-10, -10, 0), Vec(10, -10, 10), Vec(-10, -10, 10))),
        Plane(listOf(Vec(-10, -10, 0), Vec(-10, 10, 0), Vec(-10, -10, 10), Vec(-10, 10, 10))),
        Plane(listOf(Vec(10, 10, 0), Vec(-10, 10, 0), Vec(10, 10, 10), Vec(-10, 10, 10))),
        Plane(listOf(Vec(10, -10, 0), Vec(10, 10, 0), Vec(10, -10, 10), Vec(10, 10, 10))))
        balls = listOf(
            Sphere(1.0, 0.0, 4.0, 2.0, 1.0),
            Sphere(0.0, 1.0, 12.0, 2.0, 1.0),
            Sphere(0.0, 1.0, 17.0, 2.0, 1.0),
            Sphere(0.0, 1.0, 22.0, 2.0, 1.0),
            Sphere(0.0, 1.0, 27.0, 2.0, 1.0),
            Sphere(0.0, 1.0, 32.0, 2.0, 1.0),
            Sphere(0.0, 1.0, 37.0, 2.0, 1.0),
        )
        register(floor)
        walls.forEach { register(it) }
        balls.forEach { register(it) }
        balls[0].color = Color.BLUE.toVec()
        balls[1].color = Color.RED.toVec()
        balls[2].color = Color.GREEN.toVec()
        balls[3].color = Color.YELLOW.toVec()
        balls[4].color = Color.PINK.toVec()
        balls[5].color = Color.MAGENTA.toVec()
        balls[6].color = Color.ORANGE.toVec()
    }
}
fun main() {
    BouncingBall().start()
}