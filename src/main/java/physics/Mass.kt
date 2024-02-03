package physics

import Conf
import algebra.Point3d
import algebra.Vec
import framework.Camera
import framework.Vertex
import framework.interfaces.*

open class Mass(mass: Double, x: Double, y: Double, z: Double) : Point3d(x, y, z), Entity {
    // Drawable
    override var outlineRasterization: Boolean = false
    override var color: Vec? = Conf.mass_color

    // Moveable
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

    override fun render(camera: Camera) {
        camera.renderSphere(Vertex(positionVector, Vec.ones * 255, Vec.zero), 0.1f, this)
    }

    fun applyForce(force: Vec) {
        if (status == Status.Movable) acceleration.addInPlace(force / mass)
    }
}
