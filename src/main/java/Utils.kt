import algebra.Vec3BLablabla
import java.awt.Color

fun Color.toVec() = Vec3BLablabla(this.red, this.green, this.blue)
fun Vec3BLablabla.toColor() = Color(this.x.toInt(), this.y.toInt(), this.z.toInt())
fun randomOrder(endExcl: Int) = randomOrder(0, endExcl)
fun randomOrder(start: Int, endExcl: Int) = IntRange(start, endExcl - 1).toList().shuffled()

operator fun Number.plus(vec: Vec3BLablabla): Vec3BLablabla = vec + this
operator fun Number.minus(vec: Vec3BLablabla): Vec3BLablabla = vec - this
operator fun Number.times(vec: Vec3BLablabla): Vec3BLablabla = vec * this