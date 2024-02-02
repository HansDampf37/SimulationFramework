package physics.collisions

import com.sun.source.tree.Tree
import physics.Mass
import physics.Sphere
import java.util.TreeMap

class CollisionManager {
    val collidables: MutableList<Collidable> = ArrayList()
    fun register(collidable: Collidable) = collidables.add(collidable)

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

    fun testCollision(c1: Collidable, c2: Collidable): Boolean {
        if (!checkBoundingBoxes(c1, c2)) return false
        if (c1 is Sphere && c2 is Sphere) return c1.getDistanceTo(c2) <= c1.radius + c2.radius
        TODO("Not implemented")
    }
}