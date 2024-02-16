package framework

import algebra.Vec3
import algebra.Vec2
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.math.abs

class CameraTest {
    private lateinit var camera: Camera

    @BeforeEach
    fun setup() {
        camera = Camera(x = 0.0, y = 0.0, z = 0.0,
            phi = 0.0, theta = 0.0,
            zoom = 1.0 , focalLength = 1.0,100, 100)
    }

    @Test
    fun project() {
        assertEquals(Vec3(0.0,0.0,1.0), camera.lookingDirection)

        val (v, dist) = camera.project(Vec3(0.0, 0.0, 1.0))
        assertEquals(1.0, dist)
        assertEquals(Vec2(50.0, 50.0), v)

        val (v1, dist1) = camera.project(Vec3(0.0, 0.0, 5.0))
        assertEquals(5.0, dist1)
        assertEquals(Vec2(50.0, 50.0), v1)

        val (_, dist2) = camera.project(Vec3(0.0, 0.0, -1.0))
        assertEquals(Double.NEGATIVE_INFINITY, dist2)
    }

    @Test
    fun project2() {
        camera.phi = Math.PI / 2
        camera.theta = 0.0
        assertEquals(Vec3(-1.0,0.0,0.0), camera.lookingDirection)

        val (v, dist) = camera.project(Vec3(-1.0, 0.0, 0.0))
        assertEquals(1.0, dist)
        assertEquals(Vec2(50.0, 50.0), v)

        val (v1, dist1) = camera.project(Vec3(-5.0, 0.0, 0.0))
        assertEquals(5.0, dist1)
        assertEquals(Vec2(50.0, 50.0), v1)

        val (_, dist2) = camera.project(Vec3(1.0, 0.0, 0.0))
        assertEquals(Double.NEGATIVE_INFINITY, dist2)
    }

    @Test
    fun project3() {
        camera.phi = Math.PI
        camera.theta = 0.0
        assertEquals(Vec3(0.0,0.0,-1.0), camera.lookingDirection)

        val (v, dist) = camera.project(Vec3(0.0, 0.0, -1.0))
        assertEquals(1.0, dist)
        assertEquals(Vec2(50.0, 50.0), v)

        val (v1, dist1) = camera.project(Vec3(0.0, 0.0, -5.0))
        assertEquals(5.0, dist1)
        assertEquals(Vec2(50.0, 50.0), v1)

        val (_, dist2) = camera.project(Vec3(0.0, 0.0, 1.0))
        assertEquals(Double.NEGATIVE_INFINITY, dist2)
    }

    @Test
    fun project4() {
        camera.phi = 3 * Math.PI / 2
        camera.theta = 0.0
        assertEquals(Vec3(1.0,0.0,0.0), camera.lookingDirection)

        val (v, dist) = camera.project(Vec3(1.0, 0.0, 0.0))
        assertEquals(1.0, dist)
        assertEquals(Vec2(50.0, 50.0), v)

        val (v1, dist1) = camera.project(Vec3(5.0, 0.0, 0.0))
        assertEquals(5.0, dist1)
        assertEquals(Vec2(50.0, 50.0), v1)

        val (_, dist2) = camera.project(Vec3(-1.0, 0.0, 0.0))
        assertEquals(Double.NEGATIVE_INFINITY, dist2)
    }

    @Test
    fun project5() {
        camera.phi = 0.0
        camera.theta = Math.PI / 2
        assertEquals(Vec3(0.0,1.0,0.0), camera.lookingDirection)

        val (v, dist) = camera.project(Vec3(0.0, 1.0, 0.0))
        assertEquals(1.0, dist)
        assertEquals(Vec2(50.0, 50.0), v)

        val (v1, dist1) = camera.project(Vec3(0.0, 5.0, 0.0))
        assertEquals(5.0, dist1)
        assertEquals(Vec2(50.0, 50.0), v1)

        val (_, dist2) = camera.project(Vec3(0.0, -1.0, 0.0))
        assertEquals(Double.NEGATIVE_INFINITY, dist2)
    }

    @Test
    fun project6() {
        camera.phi = 0.0
        camera.theta = -Math.PI / 2
        assertEquals(Vec3(0.0,0.0,1.0), camera.lookingDirection)

        val (v, dist) = camera.project(Vec3(0.0, 0.0, 1.0))
        assertEquals(1.0, dist)
        assertEquals(Vec2(50.0, 50.0), v)

        val (v1, dist1) = camera.project(Vec3(0.0, 0.0, 5.0))
        assertEquals(5.0, dist1)
        assertEquals(Vec2(50.0, 50.0), v1)

        val (_, dist2) = camera.project(Vec3(0.0, 0.0, -1.0))
        assertEquals(Double.NEGATIVE_INFINITY, dist2)
    }

    @Test
    fun `test looking directions 1`() {
        camera.phi = Math.PI
        camera.theta = 0.0
        assertEquals(Vec3(0,0,-1), camera.lookingDirection)
        assertEquals(Vec3(0,1,0), camera.up)
        assertEquals(Vec3(-1,0,0), camera.left)
    }

    @Test
    fun `test looking directions 2`() {
        camera.phi = 0.0
        camera.theta = Math.PI / 2
        assertEquals(Vec3(0,1,0), camera.lookingDirection)
        assertEquals(Vec3(0,0,-1), camera.up)
        assertEquals(Vec3(1,0,0), camera.left)
    }

    @Suppress("unused")
    private fun assertAlmostEquals(a: Double, b: Double, eps: Double) {
        assertTrue(abs(a - b) < eps, "Expected $a does not equal actual $b")
    }
}
