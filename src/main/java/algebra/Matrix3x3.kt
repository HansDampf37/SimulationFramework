package algebra

class Matrix3x3(
    var a: Double, var b: Double, var c: Double,
    var d: Double, var e: Double, var f: Double,
    var g: Double, var h: Double, var i: Double
): IMatrix<Vec3, Vec3>() {
    operator fun times(v: Vec3): Vec3 {
        return Vec3(
            v.x * a + v.y * b + v.z * c,
            v.x * d + v.y * e + v.z * f,
            v.x * g + v.y * h + v.z * i
        )
    }

    operator fun times(m: Matrix3x4): Matrix3x4 {
        return Matrix3x4(
            m.a * a + m.e * b + m.i * c,
            m.b * a + m.f * b + m.j * c,
            m.c * a + m.g * b + m.k * c,
            m.d * a + m.h * b + m.l * c,
            m.a * d + m.e * e + m.i * f,
            m.b * d + m.f * e + m.j * f,
            m.c * d + m.g * e + m.k * f,
            m.d * d + m.h * e + m.l * f,
            m.a * g + m.e * h + m.i * i,
            m.b * g + m.f * h + m.j * i,
            m.c * g + m.g * h + m.k * i,
            m.d * g + m.h * h + m.l * i,
        )
    }

    operator fun times(m: Matrix3x3): Matrix3x3 {
        return Matrix3x3(
            m.a * a + m.d * b + m.g * c,
            m.b * a + m.e * b + m.h * c,
            m.c * a + m.f * b + m.i * c,
            m.a * d + m.d * e + m.g * f,
            m.b * d + m.e * e + m.h * f,
            m.c * d + m.f * e + m.i * f,
            m.a * g + m.d * h + m.g * i,
            m.b * g + m.e * h + m.h * i,
            m.c * g + m.f * h + m.i * i,
        )
    }

    constructor(
        a: Number, b: Number, c: Number,
        d: Number, e: Number, f: Number,
        g: Number, h: Number, i: Number
    ) : this(
        a.toDouble(), b.toDouble(), c.toDouble(),
        d.toDouble(), e.toDouble(), f.toDouble(),
        g.toDouble(), h.toDouble(), i.toDouble()
    )

    override fun getColumn(j: Int) = when (j) {
        0 -> Vec3(a, d, g)
        1 -> Vec3(b, e, h)
        2 -> Vec3(c, f, i)
        else -> throw IndexOutOfBoundsException()
    }

    override fun getRow(i: Int): Vec3 = when (i) {
        0 -> Vec3(a, b, c)
        1 -> Vec3(d, e, f)
        2 -> Vec3(g, h, i)
        else -> throw IndexOutOfBoundsException()
    }

    override val width: Int = 3
    override val height: Int = 3

    override fun get(i: Int, j: Int) = when (i) {
        0 -> Vec3(a, b, c)[j]
        1 -> Vec3(d, e, f)[j]
        2 -> Vec3(g, h, i)[j]
        else -> throw IndexOutOfBoundsException()
    }

    override fun set(i: Int, j: Int, value: Double) {
        getRow(i)[j] = value
    }

    companion object {
        val ZERO = Matrix3x3(0, 0, 0, 0, 0, 0, 0, 0, 0)
        val ONE = Matrix3x3(1, 0, 0, 0, 1, 0, 0, 0, 1)
    }
}