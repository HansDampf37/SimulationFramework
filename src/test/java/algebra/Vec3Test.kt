package algebra

import framework.times
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import plus
import kotlin.math.PI

class Vec3Test {
    private val a = Vec3(1, 0, 0)
    private val b = Vec3(0, 1, 0)
    private val c = Vec3(0, 0, 1)
    private val d = Vec3(0, 1, 1)
    private val d2 = Vec3(0, 2, 2)
    private val e = Vec4(0, 0, 0, 1)
    private val f = Vec4(0, 0, 0, -1)
    private val g = Vec4(1, 1, 1, 0)

    @Test
    fun plus() {
        assertEquals(d, b + c)
        assertEquals(g, f + 1.0)
        assertEquals(g, 1 + f)
    }

    @Test
    fun minus() {
        assertEquals(b, d - c)
        assertEquals(f, g - 1)
    }

    @Test
    operator fun unaryMinus() {
        assertEquals(e, -f)
        assertEquals(e, -(-e))
    }

    @Test
    fun times() {
        assertEquals(0.0, a * b)
        assertEquals(1.0, d * c)
        assertEquals(-1.0, f * e)
        assertEquals(d2, d * 2)
        assertEquals(d2, 2 * d)
    }

    @Test
    fun div() {
        assertEquals(d, d2 / 2)
    }

    @Test
    fun normalize() {
        val v = Vec2(2, 0)
        assertEquals(Vec2(1, 0), v.normalize())
        org.junit.jupiter.api.assertThrows<ArithmeticException> { Vec3.zero.normalize() }
    }

    @Test
    fun getLength() {
        val v = Vec3(0, 1, 0)
        assertEquals(1.0, v.length)
        assertEquals(0.0, Vec3.zero.length)
    }

    @Test
    fun projectOnto() {
        val v1 = Vec3(1, 0, 0)
        val v2 = Vec3(2, 2, 0)
        val v3 = Vec3(0, 0, 1)
        assertEquals(Vec3(2, 0, 0), v2.projectOnto(v1))
        assertEquals(Vec3(0, 0, 0), v3.projectOnto(v1))
    }

    @Test
    fun setTo() {
        val v = Vec3(1, 2, 3)
        val w = Vec3(4, 5, 6)
        assertNotEquals(v, w)
        w.setTo(v)
        assertEquals(v, w)
    }

    @Test
    fun angleWith() {
        val v1 = Vec3(1, 0, 0)
        val v2 = Vec3(0, 1, 0)
        val v3 = Vec3(-1, 0, 0)
        assertTrue(v1.angleWith(v2) - PI / 2 < 0.0001)
        assertTrue(v2.angleWith(v1) - PI / 2 < 0.0001)
        assertTrue(v2.angleWith(v3) - PI / 2 < 0.0001)
        assertTrue(v3.angleWith(v2) - PI / 2 < 0.0001)
        assertTrue(v3.angleWith(v1) - PI < 0.0001)
        assertTrue(v1.angleWith(v3) - PI < 0.0001)
    }

    @Test
    fun testEquals() {
        assertTrue(Vec3(1, 0, 0) == Vec3(1.0, 0.0, 0.0))
        assertTrue(Vec3(1, 0.0, 0) == Vec3(1.0, 0, 0.0))
        assertFalse(Vec4(1, 0, 0, 0) == Vec3(1, 0, 0))
        assertFalse(Vec3(2, 0, 0) == Vec3(1, 0, 0))
        assertTrue(Vec2(-0, -0) == Vec2(0, 0))
    }

    @Test
    fun testToString() {
        assertEquals("(3.14, 13254, 3)", Vec3(PI, 13254, 3).toString())
    }

    @Test
    fun crossProduct() {
        assertEquals(Vec3(1.0, 0.0, 0.0), Vec3(0.0, 1.0, 0.0).crossProduct(Vec3(0.0, 0.0, 1.0)))
        assertEquals(Vec3(0.0, -1.0, 0.0), Vec3(1.0, 0.0, 0.0).crossProduct(Vec3(0.0, 0.0, 1.0)))
        assertEquals(Vec3(0.0, 0.0, -1.0), Vec3(0.0, 1.0, 0.0).crossProduct(Vec3(1.0, 0.0, 0.0)))
    }
}