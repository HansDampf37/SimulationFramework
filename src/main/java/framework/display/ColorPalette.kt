package framework.display

import framework.times
import toColor
import toVec
import java.awt.Color

/**
 * A color palette is a set of Colors used to render objects. Additionally, a [Horizon] that appears as
 * in the background is defined.
 */
class ColorPalette(
    val smallObjectColor: Color,
    val bigObjectColor: Color,
    val linkColor: Color,
    val colorOutline: Color,
    val horizon: Horizon) {

    class Horizon(private val colors: List<Color>,
                  weights: List<Number> = List(colors.size) { 1.0 / colors.size }) {
        private var weights: List<Double> = weights.map{it.toDouble()}

        init {
            require(colors.isNotEmpty()) { "Horizon needs at least 1 color." }
            require(colors.size == weights.size)
            val sum = this.weights.sum()
            require(sum > 0) {"The sum of weights must be  0"}
            this.weights = this.weights.map { weight -> weight / sum }
        }
        fun gradient(start: Double, end: Double): Gradient {
            if (end <= start) throw IllegalArgumentException("end must be > than start")
            val grad = Gradient(start, end)
            var weightCount = 0.0
            val weight = this.weights
            for (i in colors.indices) {
                grad.add(Pair(colors[i], start + (end - start) * weightCount))
                if (i < weight.size) weightCount += weight[i]
            }
            return grad
        }
    }

    /**
     * A Gradient maps a sequence of colors to a range of numbers.
     */
    class Gradient(private var start: Double, private var end: Double) : ArrayList<Pair<Color, Double>>() {
        private fun getColor(double: Double): Color {
            val highestLowerEntry = this.filter { it.second < double }.maxByOrNull { it.second } ?: first()
            val lowestGreaterEntry = this.filter { it.second > double }.minByOrNull { it.second } ?: last()
            if (highestLowerEntry == lowestGreaterEntry) return highestLowerEntry.first
            val blend = (double - lowestGreaterEntry.second) / (highestLowerEntry.second - lowestGreaterEntry.second)
            return ((1 - blend) * lowestGreaterEntry.first.toVec() + blend * highestLowerEntry.first.toVec()).toColor()
        }

        fun clip(lowerBound: Double, upperBound: Double) {
            val c1 = getColor(lowerBound)
            val c2 = getColor(upperBound)
            this.removeAll { it.second <= lowerBound || it.second >= upperBound }
            this.add(0, Pair(c1, lowerBound))
            this.add(Pair(c2, upperBound))
            this.start = lowerBound
            this.end = upperBound
        }

        fun translateScale(newStart: Double, newEnd: Double) {
            for (i in indices) {
                this[i] = Pair(
                    this[i].first,
                    (this[i].second - this.start) * (newEnd - newStart) / (this.end - this.start)
                )
            }
            this.start = newStart
            this.end = newEnd
        }
    }
}