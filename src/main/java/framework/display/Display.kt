package framework.display

import com.formdev.flatlaf.FlatDarculaLaf
import framework.WatchedField
import java.awt.*
import java.text.NumberFormat
import javax.swing.*


class Display(
    title: String,
    private val height: Int = 1000,
    private val width: Int = 1600,
    private val controlWidth: Int = 500,
) {
    val window: JFrame = JFrame(title)
    val canvas: Canvas
    private val controls: ControlPanel

    init {
        FlatDarculaLaf.setup();

        window.layout = BorderLayout()
        window.setPreferredSize(Dimension(width, height))
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
        window.pack()
        window.setLocationRelativeTo(null)
        window.isVisible = true

        canvas = Canvas()
        canvas.setPreferredSize(Dimension(width - controlWidth, height))
        canvas.setMaximumSize(Dimension(width, height))
        canvas.setMinimumSize(Dimension(width - controlWidth, height))
        window.add(canvas, BorderLayout.WEST)

        controls = ControlPanel(controlWidth, height)
        window.add(controls, BorderLayout.EAST)
    }

    fun getHeight(): Int {
        return window.height
    }

    fun getWidth(): Int {
        return window.width
    }

    fun setWatchedFields(watchedFields: Map<Any, List<WatchedField<*, *>>>) {
        controls.setWatchedFields(watchedFields)
        controls.revalidate()
        controls.repaint()
    }

    companion object {
        val nf: NumberFormat = NumberFormat.getNumberInstance().apply { maximumFractionDigits = 0 }
        fun round(number: Double) = nf.format(number)
        val titleFont = Font("", Font.BOLD, 20)
        val columnNameFont = Font("", Font.BOLD, 14)
        val entryFont = Font("", Font.PLAIN, 11)
        val subsectionFont = Font("Hack", Font.ITALIC, 8)

    }
}

