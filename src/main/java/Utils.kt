import algebra.Vec
import java.awt.Color

fun Color.toVec() = Vec(this.red, this.green, this.blue)