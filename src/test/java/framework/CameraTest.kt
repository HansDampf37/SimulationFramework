package framework

import algebra.Vec
import algebra.Vec2
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CameraTest {
    private lateinit var camera: Camera

    @Test
    fun project() {
        camera = Camera(0.0, 0.0, 0.0,
            0.0, 0.0, 0.0,
            1.0, 1.0, 1.0,
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
        camera = Camera(0.0, 0.0, 0.0,
            yaw = Math.PI / 2, 0.0, 0.0,
            1.0, 1.0, 1.0,
            100, 100)
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
        camera = Camera(0.0, 0.0, 0.0,
            yaw = Math.PI, 0.0, 0.0,
            1.0, 1.0, 1.0,
            100, 100)
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
        camera = Camera(0.0, 0.0, 0.0,
            yaw = Math.PI * 3 / 2, 0.0, 0.0,
            1.0, 1.0, 1.0,
            100, 100)
        assertEquals(Vec(-1.0,0.0,0.0), camera.lookingDirection)

        val (v, dist) = camera.project(Vec(-1.0, 0.0, 0.0))
        assertEquals(1.0, dist)
        assertEquals(Vec2(50.0, 50.0), v)

        val (v1, dist1) = camera.project(Vec(-5.0, 0.0, 0.0))
        assertEquals(5.0, dist1)
        assertEquals(Vec2(50.0, 50.0), v1)

        val (_, dist2) = camera.project(Vec(1.0, 0.0, 0.0))
        assertEquals(Double.NEGATIVE_INFINITY, dist2)
    }
}