package framework.physics

import Conf
import algebra.Point3d
import algebra.Vec3
import framework.Camera
import framework.Vertex
import framework.interfaces.Mass
import framework.interfaces.Status
import toVec
import kotlin.math.cos
import kotlin.math.sin

/**
 * Implementation of [Mass].
 */
open class PointMass(x: Double, y: Double, z: Double, var radius: Double = 1.0, mass: Kg) : Point3d(x, y, z), Mass {
    // Drawable
    override var outlineRasterization: Boolean = false
    override var color: Vec3? = Conf.colorScheme.smallObjectColor.toVec()

    // Moveable
    override var velocity: Vec3 = Vec3(0, 0, 0)
    override var acceleration: Vec3 = Vec3(0, 0, 0)
    override var status = Status.Movable
    override var position: Vec3
        get() = positionVector
        set(value) {
            x = value.x
            y = value.y
            z = value.z
        }

    // mass
    override val mass: Kg = mass

    init {
        require(mass != 0.0) { "Mass can't be equal to 0" }
    }

    constructor(positionVector: Vec3, mass: Double, radius: Double = 1.0) : this(
        positionVector.x,
        positionVector.y,
        positionVector.z,
        radius,
        mass
    )

    override fun render(camera: Camera) {
        camera.renderSphere(Vertex(this.positionVector, color ?: Vec3.zero, Vec3.zero), radius.toFloat(), this)
    }
}
