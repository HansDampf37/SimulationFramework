package framework.algebra

import algebra.Vec
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class VecTest {

    @Test
    fun crossProduct() {
        assertEquals(Vec(1.0, 0.0, 0.0), Vec(0.0, 1.0, 0.0).crossProduct(Vec(0.0, 0.0, 1.0)))
        assertEquals(Vec(0.0, -1.0, 0.0), Vec(1.0, 0.0, 0.0).crossProduct(Vec(0.0, 0.0, 1.0)))
        assertEquals(Vec(0.0, 0.0, -1.0), Vec(0.0, 1.0, 0.0).crossProduct(Vec(1.0, 0.0, 0.0)))
    }
}