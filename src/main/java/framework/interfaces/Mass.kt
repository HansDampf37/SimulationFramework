package framework.interfaces

import algebra.Vec3
import framework.physics.Kg

/**
 * An [Entity] with mass.
 */
interface Mass: Entity {
    val mass: Kg

    fun applyForce(force: Vec3) {
        if (status == Status.Movable) acceleration.addInPlace(force / mass)
    }
}
