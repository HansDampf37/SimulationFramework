package algebra

class Matrix3x4(
    var a: Double, var b: Double, var c: Double, var d: Double,
    var e: Double, var f: Double, var g: Double, var h: Double,
    var i: Double, var j: Double, var k: Double, var l: Double
) {
    operator fun times(v: Vec4): Vec3BLablabla {
        return Vec3BLablabla(
            v.x * a + v.y * b + v.z * c + v.w * d,
            v.x * e + v.y * f + v.z * g + v.w * h,
            v.x * i + v.y * j + v.z * k + v.w * l
        )
    }

    operator fun minus(m: Matrix3x4): Matrix3x4 {
        return Matrix3x4(
            a - m.a, b - m.b, c - m.c, d - m.d,
            e - m.e, f - m.f, g - m.g, h - m.h,
            i - m.i, j - m.j, k - m.k, l - m.l
        )
    }

    override fun toString(): String {
        return "| $a, $b, $c, $d |\n" +
                "| $e, $f, $g, $h |\n" +
                "| $i, $j, $k, $l |"
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Matrix3x4) {
            val dif = this - other
            val eps = 0.0001
            dif.a < eps && dif.b < eps && dif.c < eps && dif.d < eps && dif.e < eps && dif.f < eps &&
                    dif.g < eps && dif.h < eps && dif.i < eps && dif.j < eps && dif.k < eps && dif.l < eps
        } else {
            false
        }
    }

    operator fun times(m: Matrix4X4): Matrix3x4 {
        return Matrix3x4(
            m.a * a + m.e * b + m.i * c + m.m * d,
            m.b * a + m.f * b + m.j * c + m.n * d,
            m.c * a + m.g * b + m.k * c + m.o * d,
            m.d * a + m.h * b + m.l * c + m.p * d,
            m.a * e + m.e * f + m.i * g + m.m * h,
            m.b * e + m.f * f + m.j * g + m.n * h,
            m.c * e + m.g * f + m.k * g + m.o * h,
            m.d * e + m.h * f + m.l * g + m.p * h,
            m.a * i + m.e * j + m.i * k + m.m * l,
            m.b * i + m.f * j + m.j * k + m.n * l,
            m.c * i + m.g * j + m.k * k + m.o * l,
            m.d * i + m.h * j + m.l * k + m.p * l
        )
    }
}