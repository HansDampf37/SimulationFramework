package spacesimulation.physics

import spacesimulation.Entity
import spacesimulation.Graphics3d
import java.awt.Color
import java.awt.Graphics

open class Collidable(
    x: Double,
    y: Double,
    z: Double,
    private val radius: Double, mass: Double): Mass(mass, x, y, z) {
    fun testForCollision(other: Collidable): Boolean {
        return this.getDistanceTo(other) < this.radius + other.radius;
    }
}

class Sphere(
    x: Double,
    y: Double,
    z: Double,
    private val radius: Double, mass: Double): Collidable(x, y, z, radius, mass), Entity {

    override fun render(drawer: Graphics3d, g: Graphics) {
        drawer.drawDot(this, radius = radius, Color.red, g)
    }
}