package framework.interfaces

import algebra.Vec
import physics.collisions.CollisionManager


/**
 * A collidable is a [Mass] that can be collided with.
 * The calculation of the collision is done in the [CollisionManager]. Instances of this class must implement a
 * [supportFunction]. The support function should return the point (in world coordinates) on the surface of the
 * Collidable that is furthest away from the collidable's centre in the specified direction.
 */
interface Collidable: Mass {
    /**
     * This function should return the point on the collidables surface that is furthest away from
     * the center and that lays in the specified [direction] in world coordinates (dot-product > 0).
     */
    fun supportFunction(direction: Vec): Vec
}