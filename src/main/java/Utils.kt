import algebra.Vec3
import java.awt.Color

fun Color.toVec() = Vec3(this.red, this.green, this.blue)
fun Vec3.toColor() = Color(this.x.toInt(), this.y.toInt(), this.z.toInt())
fun randomOrder(endExcl: Int) = randomOrder(0, endExcl)
fun randomOrder(start: Int, endExcl: Int) = IntRange(start, endExcl - 1).toList().shuffled()

operator fun Number.plus(vec: Vec3): Vec3 = vec + this
operator fun Number.minus(vec: Vec3): Vec3 = vec - this
operator fun Number.times(vec: Vec3): Vec3 = vec * this