package spacesimulation.physics

import spacesimulation.algebra.Vec
import kotlin.math.pow

/**
 * Use [Collision.occur] to calculate the new velocities after the collision and the amount of energy transmitted.
 */
class Collision {
    companion object{
        /**
         * Simulates the (inelastic) collision of two masses with k being the coefficient of restitution.
         * **See** [wikipedia](https://en.wikipedia.org/wiki/Inelastic_collision)
         * @param mass1 the first mass
         * @param mass2 the second mass
         * @param k the coefficient of restitution
         * @return energy transmitted in the collision
         */
        fun occur(mass1: Mass, mass2: Mass, k: Double = 1.0): Double {
            val dir = mass1.getDirectionTo(mass2)
            if (dir.length == 0.0) return 0.0
            val v1 = Vec.linearProjection(mass1.velocity, dir)
            val v2 = Vec.linearProjection(mass2.velocity, dir)
            val v1O = mass1.velocity - v1
            val v2O = mass2.velocity - v2
            if (listOf(mass1, mass2).all { it.status == Mass.Status.Movable }) {
                val m1: Double = mass1.mass
                val m2: Double = mass2.mass
                val v1New = (v1 * m1 + v2 * m2 - (v1 - v2) * m2 * k) / (m1 + m2)
                val v2New = (v1 * m1 + v2 * m2 - (v2 - v1) * m1 * k) / (m1 + m2)
                mass1.velocity = v1New + v1O
                mass2.velocity = v2New + v2O
                return m1 * m2 / (2 * m1 + 2 * m2) * (v1.length - v2.length).pow(2) * (1-k.pow(2))
            } else if (mass1.status == Mass.Status.Immovable && mass2.status == Mass.Status.Movable) {
                // if mass 1 is not movable treat it like its mass is infinity
                mass2.velocity = v1 + (v1 - v2) * k + v2O
                return (k-1).pow(2)/2 * mass1.mass * (v1.length - v2.length).pow(2)
            } else if (mass1.status == Mass.Status.Movable && mass2.status == Mass.Status.Immovable) {
                // if mass 2 is not movable treat it like its mass is infinity
                mass1.velocity = v2 + (v2 - v1) * k + v1O
                return (k-1).pow(2)/2 * mass2.mass * (v1.length - v2.length).pow(2)
            }
            return 0.0
        }
    }
}