package algebra

class Matrix4X4(
    var a: Double, var b: Double, var c: Double, var d: Double,
    var e: Double, var f: Double, var g: Double, var h: Double,
    var i: Double, var j: Double, var k: Double, var l: Double,
    var m: Double, var n: Double, var o: Double, var p: Double
) {
    operator fun times(v: Vec4): Vec4 {
        return Vec4(
            v.x * a + v.y * b + v.z * c + v.w * d,
            v.x * e + v.y * f + v.z * g + v.w * h,
            v.x * i + v.y * j + v.z * k + v.w * l,
            v.x * m + v.y * n + v.z * o + v.w * p
        )
    }

    operator fun times(m: Matrix4X4): Matrix4X4 {
        return Matrix4X4(
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
            m.d * i + m.h * j + m.l * k + m.p * l,
            m.a * this.m + m.e * n + m.i * o + m.m * p,
            m.b * this.m + m.f * n + m.j * o + m.n * p,
            m.c * this.m + m.g * n + m.k * o + m.o * p,
            m.d * this.m + m.h * n + m.l * o + m.p * p
        )
    }

    operator fun times(scalar: Number): Matrix4X4 {
        return Matrix4X4(
            a * scalar.toDouble(), b * scalar.toDouble(), c * scalar.toDouble(), d * scalar.toDouble(),
            e * scalar.toDouble(), f * scalar.toDouble(), g * scalar.toDouble(), h * scalar.toDouble(),
            i * scalar.toDouble(), j * scalar.toDouble(), k * scalar.toDouble(), l * scalar.toDouble(),
            m * scalar.toDouble(), n * scalar.toDouble(), o * scalar.toDouble(), p * scalar.toDouble(),
        )
    }

    operator fun plus(mat: Matrix4X4): Matrix4X4 {
        return Matrix4X4(
            this.a + mat.a, this.b + mat.b, this.c + mat.c, this.d + mat.d,
            this.e + mat.e, this.f + mat.f, this.g + mat.g, this.h + mat.h,
            this.i + mat.i, this.j + mat.j, this.k + mat.k, this.l + mat.l,
            this.m + mat.m, this.n + mat.n, this.o + mat.o, this.p + mat.p
        )
    }

    fun transpose() = Matrix4X4(
        a, e, i, m,
        b, f, j, n,
        c, g, k, o,
        d, h, l, p
    )

    override fun toString(): String {
        return "| $a, $b, $c, $d |\n" +
                "| $e, $f, $g, $h |\n" +
                "| $i, $j, $k, $l |\n" +
                "| $m, $n, $o, $p |"
    }
}