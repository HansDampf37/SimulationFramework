package framework.physics.collisions

import algebra.Vec3BLablabla
import framework.interfaces.Collidable
import framework.interfaces.Status
import framework.physics.Sphere
import java.lang.IllegalStateException

/**
 * The collision manager holds state about a set of [Collidable]s.
 *
 * A collidable can be registered at the collision manager by calling [register].
 *
 * A collidable can be removed from the collision manager by calling [unregister].
 *
 * All registered collidables can be removed by the calling [reset].
 *
 * Calculations between pairs of registered collidables can be calculated by calling [calculateCollisions].
 * Internally, the GJK algorithm is used to check for collisions. TODO In the future a BVH should be used.
 *
 * **see** [GJK-Algorithm](https://www.youtube.com/watch?v=ajv46BSqcK4)
 */
class CollisionManager {
    private val collidables: MutableList<Collidable> = ArrayList()
    fun register(collidable: Collidable) = synchronized(collidables) {collidables.add(collidable) }

    fun unregister(collidable: Collidable) = synchronized(collidables) { collidables.remove(collidable) }

    fun reset() = synchronized(collidables) { collidables.clear() }

    fun calculateCollisions() {
        synchronized(collidables) {
            collidables.forEach { c1 ->
                collidables.forEach { c2 ->
                    if (c1 != c2) {
                        if (testCollision(c1, c2)) {
                            Collision.occur(c1, c2, 1.0)
                            if (c1 is Sphere && c2 is Sphere) {
                                val targetDistance = c1.radius + c2.radius
                                val overlap = targetDistance - c1.getDistanceTo(c2)
                                val massMovable = c2.status == Status.Movable
                                val overlap1 = if (massMovable) c1.mass / (c2.mass + c1.mass) * overlap else 0.0
                                val overlap2 = if (massMovable) c2.mass / (c2.mass + c1.mass) * overlap else overlap
                                if ((c2.positionVector - c1.positionVector).length != 0.0) {
                                    c1.set(c1 + c2.getDirectionTo(c1) * overlap2)
                                    c2.set(c2 + c1.getDirectionTo(c2) * overlap1)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun testCollision(c1: Collidable, c2: Collidable): Boolean {
        if (c1 is Sphere && c2 is Sphere) return c1.getDistanceTo(c2) <= c1.radius + c2.radius
        TODO("Not implemented")
    }

    private fun support(c1: Collidable, c2: Collidable, direction: Vec3BLablabla): Vec3BLablabla {
        val support1 = c1.supportFunction(direction)
        val support2 = c2.supportFunction(-direction)
        return support1 - support2
    }

    private fun tripleProduct(a: Vec3BLablabla, b: Vec3BLablabla, c: Vec3BLablabla): Vec3BLablabla {
        // (a x b) x c = b * (a · c) - a * (b · c)
        val crossAB = a.crossProduct(b)
        return Vec3BLablabla(
            crossAB.x * c.x - crossAB.y * c.y,
            crossAB.x * c.y + crossAB.y * c.x - crossAB.z * c.z,
            crossAB.x * c.z + crossAB.z * c.x + crossAB.y * c.y
        )
    }

    private fun handleSimplex(simplex: MutableList<Vec3BLablabla>, direction: Vec3BLablabla): Boolean {
        when (simplex.size) {
            2 -> {
                val b = simplex[0]
                val a = simplex[1]
                val ao = -a
                val ab = b - a
                // if simplex is a line we change our search direction orthogonally to the line in the direction
                // of the origin
                val abPerp = ab.crossProduct(ao).crossProduct(ab)
                if (abPerp.length == 0.0) {
                    // ab and ao are parallel -> zero is in simplex
                    return true
                }
                direction.setTo(abPerp.normalize())
                return false
            }
            3 -> {
                val c = simplex[0]
                val b = simplex[1]
                val a = simplex[2]
                val ab = b - a
                val ac = c - a
                val ao = -a
                // if simplex is a triangle we change our search direction orthogonally to the triangle in the direction
                // of the origin
                val abcPerp = ab.crossProduct(ac)
                if (abcPerp.dotProduct(ao) > 0) {
                    direction.setTo(abcPerp.normalize())
                } else if (abcPerp.dotProduct(ao) < 0) {
                    direction.setTo(-abcPerp.normalize())
                } else {
                    // origin is on triangle surface
                    return true
                }
                return false
            }
            4 -> {
                val a = simplex[3]
                val b = simplex[2]
                val c = simplex[1]
                val d = simplex[0]

                val ab = b - a
                val ac = c - a
                val ad = d - a
                val ao = -a

                val abc = tripleProduct(ab, ac, ao)
                val acd = tripleProduct(ac, ad, ao)
                val adb = tripleProduct(ad, ab, ao)

                var normalAbc = ac.crossProduct(ab)
                normalAbc = if (normalAbc.dotProduct(d) < 0) normalAbc else -normalAbc
                var normalAcd = ac.crossProduct(ad)
                normalAcd = if (normalAcd.dotProduct(b) < 0) normalAcd else -normalAcd
                var normalAdb = ad.crossProduct(ab)
                normalAdb = if (normalAdb.dotProduct(c) < 0) normalAdb else -normalAdb

                return if (normalAbc.dotProduct(ao) > 0) {
                    simplex.removeAt(0)
                    direction.setTo(abc)
                    false
                } else if (normalAcd.dotProduct(ao) > 0) {
                    simplex.removeAt(1)
                    direction.setTo(acd)
                    false
                } else if (normalAdb.dotProduct(ao) > 0) {
                    simplex.removeAt(2)
                    direction.setTo(adb)
                    false
                } else {
                    // The origin is inside the tetrahedron
                    true
                }
            }
            else -> {
                throw IllegalStateException("Simplex should not be size > 4")
            }
        }
    }

    private fun gjk(c1: Collidable, c2: Collidable): Boolean {
        val simplex: MutableList<Vec3BLablabla> = mutableListOf()
        if (c1.position == c2.position) return true
        val initialDirection = (c1.position - c2.position).normalize()
        var supportVector = support(c1, c2, initialDirection)
        simplex.add(supportVector)
        val direction = -initialDirection
        while (true) {
            supportVector = support(c1, c2, direction)
            // If the new point is on the same side as the origin, the two shapes do not intersect
            if (supportVector.dotProduct(direction) <= 0) return false
            simplex.add(supportVector)
            // Check for intersection
            if (handleSimplex(simplex, direction)) return true
        }
    }

    /*private fun testCollision(c1: Collidable, c2: Collidable): Boolean {
        // Call the GJK algorithm
        return gjk(c1, c2)
    }*/
}