package framework.interfaces

import algebra.Vec
import physics.Kg

/**
 * An [Entity] with mass.
 */
interface Mass: Entity, Tickable {
    val mass: Kg

    fun applyForce(force: Vec) {
        if (status == Status.Movable) acceleration.addInPlace(force / mass)
    }
}
