package simulations

import framework.MassSimulation
import physics.Seconds
import physics.Sphere
import kotlin.math.PI

class JustASphere(): MassSimulation<Sphere>("Sphere") {
    private val sphere = Sphere(0.0, 0.0, 0.0, 1.0, 1.0)
    init {
        reset()
    }
    override fun render() {
        sphere.render(camera)
    }

    override fun reset() {
        sphere.x = 0.0
        sphere.y = 0.0
        sphere.z = 0.0
        camera.focalLength = 10.0
        camera.x = 0.0
        camera.y = -25.0
        camera.z = 0.0
        camera.theta = PI / 2
        camera.phi = PI
        camera.focalLength = 10.0
        camera.zoom = 0.001
    }

    override fun correct() = Unit

    override fun calcForces(dt: Seconds) = Unit
}