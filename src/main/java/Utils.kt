import algebra.Vec3
import java.awt.Color
import java.text.DecimalFormat
import kotlin.math.ceil
import kotlin.math.log10
import kotlin.math.max

fun Color.toVec() = Vec3(this.red, this.green, this.blue)
fun Vec3.toColor() = Color(this.x.toInt(), this.y.toInt(), this.z.toInt())
fun randomOrder(endExcl: Int) = randomOrder(0, endExcl)
fun randomOrder(start: Int, endExcl: Int) = IntRange(start, endExcl - 1).toList().shuffled()

operator fun Number.plus(vec: Vec3): Vec3 = vec + this
operator fun Number.minus(vec: Vec3): Vec3 = vec - this
operator fun Number.times(vec: Vec3): Vec3 = vec * this

fun Double.format(digitsBeforeComma: Int? = null, digitsAfterComma: Int = 2): String {
    val formatter = if (digitsBeforeComma != null) {
        DecimalFormat("0".repeat(max(1, digitsBeforeComma)) + "." + "0".repeat(digitsAfterComma))
    } else {
        DecimalFormat("0." + "0".repeat(digitsAfterComma))
    }
    return formatter.format(this).replace(",", ".").replaceLeadingZerosWithSpaces().removeSuffix(".")
}

fun String.replaceLeadingZerosWithSpaces(): String {
    return this.replaceFirst("^0+(?!\\.)".toRegex(), " ".repeat(this.length - this.trimStart('0').length))
}

fun Double.digitsBeforeComma() = ceil(log10(this)).toInt()
fun Double.digitsAfterComma() = toString().removeSuffix(".0").substringAfter(".", "").length