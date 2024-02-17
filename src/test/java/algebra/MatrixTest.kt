package algebra

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MatrixTest {

    @Test
    fun column() {
        val m = Matrix3x3(1, 2, 3, 4, 5, 6 ,7 ,8 ,9)
        assertEquals(m.getColumn(0), Vec3(1, 4, 7))
        assertEquals(m.getColumn(1), Vec3(2, 5, 8))
        assertEquals(m.getColumn(2), Vec3(3, 6, 9))
    }

    @Test
    fun times() {
        val m = Matrix3x3(1, 2, 3, 4, 5, 6, 7, 8, 9)
        val e = Matrix3x3.ONE
        val o = Matrix3x3.ZERO

        assertEquals(m.getColumn(0), m * Vec3(1, 0, 0))
        assertEquals(m.getColumn(1), m * Vec3(0, 1, 0))
        assertEquals(m.getColumn(2), m * Vec3(0, 0, 1))
        assertEquals(Vec3.zero, m * Vec3.zero)

        assertEquals(m, m * e)
        assertEquals(o, m * o)
    }


    @Test
    fun testToString() {
        val str = "| 1.000, 2.000,      3|\n" +
                "|40.387, 5.403,      6|\n" +
                "| 7.000, 8.000, 910493|"
        assertEquals(str, Matrix3x3(1, 2, 3, 40.387, 5.40320000001, 6, 7, 8, 910493).toString())
    }
}