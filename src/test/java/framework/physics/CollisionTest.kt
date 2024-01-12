package framework.physics

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import physics.Collision
import physics.Mass
import algebra.Vec

class CollisionTest {
    @Test
    fun `equal masses one moving one resting`() {
        val m1 = Mass(1.0, Vec(.0, .0, .0))
        val m2 = Mass(1.0, Vec(1.0, .0, .0))
        m1.velocity = Vec(1.0, 0.0, 0.0)
        m2.velocity = Vec(0.0, 0.0, 0.0)
        Collision.occur(m1, m2)
        assertEquals(m1.velocity, Vec(0.0, 0.0, 0.0))
        assertEquals(m2.velocity, Vec(1.0, 0.0, 0.0))
    }

    @Test
    fun `equal masses both moving`() {
        val m1 = Mass(1.0, Vec(.0, .0, .0))
        val m2 = Mass(1.0, Vec(1.0, .0, .0))
        m1.velocity = Vec(0.5, 0.0, 0.0)
        m2.velocity = Vec(-0.5, 0.0, 0.0)
        Collision.occur(m1, m2)
        assertEquals(m1.velocity, Vec(-0.5, 0.0, 0.0))
        assertEquals(m2.velocity, Vec(0.5, 0.0, 0.0))
    }

    @Test
    fun `no collision occurs if the velocities are orthogonal to connecting vector`() {
        val m1 = Mass(1.0, Vec(.0, .0, .0))
        val m2 = Mass(1.0, Vec(1.0, .0, .0))
        m1.velocity = Vec(0.0, 0.5, 0.0)
        m2.velocity = Vec(0.0, -0.5, 0.0)
        Collision.occur(m1, m2)
        assertEquals(m1.velocity, Vec(0.0, 0.5, 0.0))
        assertEquals(m2.velocity, Vec(0.0, -0.5, 0.0))
    }
}