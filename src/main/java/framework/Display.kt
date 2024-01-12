package framework

import java.awt.Canvas
import java.awt.Color
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.WindowConstants

class Display(val title: String, private var height: Int = 1000, private var width: Int = 1600) {
    val jFrame: JFrame = JFrame("Title")
    val canvas: Canvas

    init {
        jFrame.setPreferredSize(Dimension(width, height))
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
        jFrame.pack()
        jFrame.setLocationRelativeTo(null)
        jFrame.isVisible = true
        canvas = Canvas()
        canvas.setPreferredSize(Dimension(width, height))
        canvas.setMaximumSize(Dimension(width, height))
        canvas.setMinimumSize(Dimension(width, height))
        canvas.setBackground(Color(42, 55, 71))
        jFrame.add(canvas)
        jFrame.pack()
    }

    fun getHeight(): Int {
        return jFrame.height ?: 1080
    }

    fun getWidth(): Int {
        return jFrame.width ?: 1920
    }
}