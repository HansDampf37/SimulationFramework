import algebra.Vec
import java.awt.Color

fun Color.toVec() = Vec(this.red, this.green, this.blue)
fun randomOrder(endExcl: Int) = randomOrder(0, endExcl)
fun randomOrder(start: Int, endExcl: Int) = IntRange(start, endExcl - 1).toList().shuffled()