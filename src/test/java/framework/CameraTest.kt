package framework

import algebra.Vec
import algebra.Vec2
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CameraTest {
    private lateinit var camera: Camera

    @BeforeEach
    fun setup() {
        camera = Camera(0.0, 0.0, 0.0,
            1.0, 1.0,
            1.0,100, 100)
    }

    @Test
    fun project() {
        camera = Camera(0.0, 0.0, 0.0,
            1.0, 1.0,
            1.0,
            100, 100)
        assertEquals(Vec(0.0,0.0,1.0), camera.lookingDirection)

        val (v, dist) = camera.project(Vec(0.0, 0.0, 1.0))
        assertEquals(1.0, dist)
        assertEquals(Vec2(50.0, 50.0), v)

        val (v1, dist1) = camera.project(Vec(0.0, 0.0, 5.0))
        assertEquals(5.0, dist1)
        assertEquals(Vec2(50.0, 50.0), v1)

        val (_, dist2) = camera.project(Vec(0.0, 0.0, -1.0))
        assertEquals(Double.NEGATIVE_INFINITY, dist2)
    }

    @Test
    fun project2() {
        camera.turnAngle = -Math.PI / 2
        camera.nodAngle = 0.0
        assertEquals(Vec(1.0,0.0,0.0), camera.lookingDirection)

        val (v, dist) = camera.project(Vec(1.0, 0.0, .0))
        assertEquals(1.0, dist)
        assertEquals(Vec2(50.0, 50.0), v)

        val (v1, dist1) = camera.project(Vec(5.0, 0.0, 0.0))
        assertEquals(5.0, dist1)
        assertEquals(Vec2(50.0, 50.0), v1)

        val (_, dist2) = camera.project(Vec(-1.0, 0.0, 0.0))
        assertEquals(Double.NEGATIVE_INFINITY, dist2)
    }

    @Test
    fun project3() {
        camera.turnAngle = Math.PI
        camera.nodAngle = 0.0
        assertEquals(Vec(0.0,0.0,-1.0), camera.lookingDirection)

        val (v, dist) = camera.project(Vec(0.0, 0.0, -1.0))
        assertEquals(1.0, dist)
        assertEquals(Vec2(50.0, 50.0), v)

        val (v1, dist1) = camera.project(Vec(0.0, 0.0, -5.0))
        assertEquals(5.0, dist1)
        assertEquals(Vec2(50.0, 50.0), v1)

        val (_, dist2) = camera.project(Vec(0.0, 0.0, 1.0))
        assertEquals(Double.NEGATIVE_INFINITY, dist2)
    }

    @Test
    fun project4() {
        camera.turnAngle = 3 * Math.PI / 2
        camera.nodAngle = 0.0
        assertEquals(Vec(1.0,0.0,0.0), camera.lookingDirection)

        val (v, dist) = camera.project(Vec(1.0, 0.0, 0.0))
        assertEquals(1.0, dist)
        assertEquals(Vec2(50.0, 50.0), v)

        val (v1, dist1) = camera.project(Vec(5.0, 0.0, 0.0))
        assertEquals(5.0, dist1)
        assertEquals(Vec2(50.0, 50.0), v1)

        val (_, dist2) = camera.project(Vec(-1.0, 0.0, 0.0))
        assertEquals(Double.NEGATIVE_INFINITY, dist2)
    }

    @Test
    fun project5() {
        camera.turnAngle = 0.0
        camera.nodAngle = Math.PI / 2
        assertEquals(Vec(0.0,1.0,0.0), camera.lookingDirection)

        val (v, dist) = camera.project(Vec(0.0, 1.0, 0.0))
        assertEquals(1.0, dist)
        assertEquals(Vec2(50.0, 50.0), v)

        val (v1, dist1) = camera.project(Vec(0.0, 5.0, 0.0))
        assertEquals(5.0, dist1)
        assertEquals(Vec2(50.0, 50.0), v1)

        val (_, dist2) = camera.project(Vec(0.0, -1.0, 0.0))
        assertEquals(Double.NEGATIVE_INFINITY, dist2)
    }

    @Test
    fun project6() {
        camera.turnAngle = 0.0
        camera.nodAngle = -Math.PI / 2
        assertEquals(Vec(0.0,-1.0,0.0), camera.lookingDirection)

        val (v, dist) = camera.project(Vec(0.0, -1.0, 0.0))
        assertEquals(1.0, dist)
        assertEquals(Vec2(50.0, 50.0), v)

        val (v1, dist1) = camera.project(Vec(0.0, -5.0, 0.0))
        assertEquals(5.0, dist1)
        assertEquals(Vec2(50.0, 50.0), v1)

        val (_, dist2) = camera.project(Vec(0.0, 1.0, 0.0))
        assertEquals(Double.NEGATIVE_INFINITY, dist2)
    }

    @Test
    fun `test default yaw pitch and roll`() {
        assertEquals(0.0, camera.yaw)
        assertEquals(0.0, camera.pitch)
        assertEquals(0.0, camera.roll)
    }

    @Test
    fun `test single yaw`() {
        camera.turnAngle = Math.PI
        camera.nodAngle = 0.0
        assertEquals(Math.PI, camera.yaw)
        assertEquals(0.0, camera.pitch)
        assertEquals(0.0, camera.roll)
    }

    @Test
    fun `test single pitch`() {
        camera.turnAngle = 0.0
        camera.nodAngle = Math.PI / 2
        assertEquals(0.0, camera.yaw)
        assertEquals(Math.PI / 2, camera.pitch)
        assertEquals(0.0, camera.roll)
    }

    @Test
    fun `test that pitch and yaw become pitch and roll`() {
        camera.turnAngle = Math.PI / 2
        camera.nodAngle = Math.PI / 2
        assertEquals(0.0, camera.yaw)
        assertEquals(Math.PI / 2, camera.pitch)
        assertEquals(Math.PI / 2, camera.roll)
        camera.nodAngle -= Math.PI / 2
        assertEquals(Math.PI / 2, camera.yaw)
        assertEquals(0.0, camera.pitch)
        assertEquals(0.0, camera.roll)

        camera.turnAngle = -Math.PI / 2
        camera.nodAngle = Math.PI / 2
        assertEquals(-0.0, camera.yaw)
        assertEquals(Math.PI / 2, camera.pitch)
        assertEquals(-Math.PI / 2, camera.roll)
    }
}