package simulations

import algebra.Vec3
import framework.interfaces.Status
import framework.physics.ImpulseConnection
import framework.physics.PhysicsSimulation
import framework.physics.Sphere
import toVec
import java.awt.Color
import kotlin.math.PI

class ShotPutPendulum: PhysicsSimulation("Bounce") {
    init {
        setup()
        configureCamera()
    }

    override fun setup() {
        val colors = listOf(
            Color.BLUE.toVec(), Color.RED.toVec(), Color.GREEN.toVec(), Color.YELLOW.toVec(),
            Color.ORANGE.toVec(), Color.MAGENTA.toVec(),Color.PINK.toVec()
        )
        val balls = List(7) {Sphere(it.toDouble(), 0.0, 0.0, 0.5, 1.0).apply { color = colors[it] }}
        val anchors = List(7) {Sphere(it.toDouble(), 0.0, 4.0, 0.0, 1.0).apply { status = Status.Immovable }}
        val links = List(7) { ImpulseConnection(balls[it], anchors[it], 4.0, 1000.0) }
        balls[0].set(Vec3(-4, 0.0, 4.0))

        balls.forEach { register(it) }
        anchors.forEach { register(it) }
        links.forEach { register(it) }
    }

    private fun configureCamera() {
        camera.focalLength = 10.0
        camera.x = 0.0
        camera.y = -37.0
        camera.z = 0.5
        camera.phi = PI
        camera.theta = PI / 2
        camera.focalLength = 10.0
        camera.zoom = 0.01
    }
}
fun main() {
    ShotPutPendulum().start()
}