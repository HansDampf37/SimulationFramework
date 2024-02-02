package physics.collisions

import com.sun.source.tree.Tree
import java.util.TreeMap

object CollisionManager {
    private val collidables: MutableList<Collidable> = ArrayList()
    fun register(collidable: Collidable) = collidables.add(collidable)

    private fun checkBoundingBoxes(c1: Collidable, c2: Collidable) {
        val bb1 = c1.getBoundingBox()
        val bb2 = c2.getBoundingBox()
        return bb1. minX <= bb2.minX && bb2.minX <= bb1.maxX && // bb2.min is between
    }
}