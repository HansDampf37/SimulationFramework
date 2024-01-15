package algebra

class Matrix4X4(var a: Double, var b: Double, var c: Double, var d: Double,
                var e: Double, var f: Double, var g: Double, var h: Double,
                var i: Double, var j: Double, var k: Double, var l: Double,
                var m: Double, var n: Double, var o: Double, var p: Double) {
    operator fun times(v: Vec4) : Vec4 {
        return Vec4(
            v.x * a + v.y * b + v.z * c + v.w * d,
            v.x * e + v.y * f + v.z * g + v.w * h,
            v.x * i + v.y * j + v.z * k + v.w * l,
            v.x * m + v.y * n + v.z * o + v.w * p
        )
    }

    override fun toString(): String {
        return  "| $a, $b, $c, $d |\n" +
                "| $e, $f, $g, $h |\n" +
                "| $i, $j, $k, $l |\n" +
                "| $m, $n, $o, $p |"
    }
}

class Matrix3x4(var a: Double, var b: Double, var c: Double, var d: Double,
                var e: Double, var f: Double, var g: Double, var h: Double,
                var i: Double, var j: Double, var k: Double, var l: Double) {
    operator fun times(v: Vec4) : Vec {
        return Vec(
            v.x * a + v.y * b + v.z * c + v.w * d,
            v.x * e + v.y * f + v.z * g + v.w * h,
            v.x * i + v.y * j + v.z * k + v.w * l
        )
    }

    override fun toString(): String {
        return  "| $a, $b, $c, $d |\n" +
                "| $e, $f, $g, $h |\n" +
                "| $i, $j, $k, $l |"
    }
}