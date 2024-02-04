package framework.interfaces

import physics.collisions.BoundingBox

interface Collidable: Mass {
    fun getBoundingBox(): BoundingBox
}