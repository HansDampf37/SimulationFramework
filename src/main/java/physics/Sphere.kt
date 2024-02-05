package physics

import algebra.Vec
import framework.interfaces.Collidable

/**
 * A Sphere is a round [PointMass] that can be [collided][Collidable] with.
 */
class Sphere(x: Double, y: Double, z: Double, radius: Double, mass: Double): PointMass(mass, x, y, z, radius), Collidable {
    override fun supportFunction(direction: Vec): Vec = this + direction.normalize() * radius
}