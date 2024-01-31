package physics

import Conf
import algebra.Point3d
import algebra.Vec
import framework.Camera
import framework.Vertex
import framework.interfaces.Entity
import framework.interfaces.Status

open class Mass(mass: Double, x: Double, y: Double, z: Double) : Point3d(x, y, z), Entity {
    // Drawable
    override var outlineRasterization: Boolean = false
    override var color: Vec? = Conf.mass_color

    // Entity
    override var velocity: Vec = Vec(0, 0, 0)
    override var acceleration: Vec = Vec(0, 0, 0)
    override var status = Status.Movable
    override var position: Vec
        get() = positionVector
        set(value) {
            x = value.x
            y = value.y
            z = value.z
        }

    // Mass
    val mass: Double

    init {
        require(mass != 0.0) { "Mass can't be equal to 0" }
        this.mass = mass
    }

    constructor(mass: Double, pos: Point3d) : this(mass, pos.x, pos.y, pos.z)

    constructor(mass: Double, positionVector: Vec) : this(mass, positionVector.x, positionVector.y, positionVector.z)

    override fun tick(dt: Seconds) {
        accelerate(dt)
        move(dt)
    }

    override fun render(camera: Camera) {
        camera.renderSphere(Vertex(positionVector, Vec.ones * 255, Vec.zero), 0.1f, this)
    }

    private fun accelerate(dt: Seconds) {
        velocity += acceleration * dt
        acceleration.setToZero()
    }

    private fun move(dt: Seconds) {
        add(velocity * dt)
    }

    fun applyForce(force: Vec) {
        acceleration.addInPlace(force / mass)
    }

    fun accelerate(acceleration: Vec) {
        this.acceleration.addInPlace(acceleration)
    }

    fun removeAccelerationInDirection(direction: Vec) {
        acceleration.subInPlace(acceleration.projectOnto(direction))
    }

    val impulse: Vec
        get() = velocity * mass
}
