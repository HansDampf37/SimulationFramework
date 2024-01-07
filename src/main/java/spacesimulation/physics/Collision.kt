package spacesimulation.physics

import spacesimulation.algebra.Vec

class Collision {
    companion object{
        /**
         * Simulates the (inelastic) collision of two masses with k being the coefficient of restitution.
         * **See** [wikipedia](https://en.wikipedia.org/wiki/Inelastic_collision)
         * @param mass1 the first mass
         * @param mass2 the second mass
         * @param k the coefficient of restitution
         */
        fun occur(mass1: Mass, mass2: Mass, k: Double = 1.0) {
            val dir = mass1.getDirectionTo(mass2)
            val v1 = Vec.linearProjection(mass1.velocity, dir)
            val v2 = Vec.linearProjection(mass2.velocity, dir)
            val v1O = Vec.sub(mass1.velocity, v1)
            val v2O = Vec.sub(mass2.velocity, v2)
            require(!(Vec.sub(Vec.add(v1, v1O), mass1.velocity).length > 0.1)) { "error" }
            require(!(Vec.sub(Vec.add(v2, v2O), mass2.velocity).length > 0.1)) { "error" }
            val m1: Double = mass1.mass
            val m2: Double = mass2.mass
            val m1v1 = Vec.scale(v1, m1)
            val m2v2 = Vec.scale(v2, m2)
            val m2DifK = Vec.scale(Vec.sub(v1, v2), m2 * k)
            val m1DifK = Vec.scale(Vec.sub(v2, v1), m1 * k)
            val v1New = Vec.scale(Vec.sub(Vec.add(m1v1, m2v2), m2DifK), 1/(m1 + m2))
            val v2New = Vec.scale(Vec.sub(Vec.add(m1v1, m2v2), m1DifK), 1/(m1 + m2))
            if (mass1.status == Mass.Status.Movable) mass1.velocity = v1New.add(v1O)
            if (mass2.status == Mass.Status.Movable) mass2.velocity = v2New.add(v2O)
        }
    }
}