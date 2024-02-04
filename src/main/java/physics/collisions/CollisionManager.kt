package physics.collisions

import framework.interfaces.Collidable
import framework.interfaces.Status
import physics.Sphere

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

    /**
     * Checks for each dimension that the dimension overlaps.
     *
     * x x o o -> no overlap
     *
     * x o x o -> overlap
     *
     * x o o x -> overlap
     *
     * o x x o -> overlap
     *
     * o x o x -> overlap
     *
     * o o x x -> no overlap
     */
    private fun checkBoundingBoxes(c1: Collidable, c2: Collidable): Boolean {
        val box1 = c1.getBoundingBox()
        val box2 = c2.getBoundingBox()
        val noOverlapX = box1.maxX < box2.minX || box1.minX > box2.maxX
        val noOverlapY = box1.maxY < box2.minY || box1.minY > box2.maxY
        val noOverlapZ = box1.maxZ < box2.minZ || box1.minZ > box2.maxZ

        return !(noOverlapX || noOverlapY || noOverlapZ)
    }

    private fun testCollision(c1: Collidable, c2: Collidable): Boolean {
        if (!checkBoundingBoxes(c1, c2)) return false
        if (c1 is Sphere && c2 is Sphere) return c1.getDistanceTo(c2) <= c1.radius + c2.radius
        TODO("Not implemented")
    }
}