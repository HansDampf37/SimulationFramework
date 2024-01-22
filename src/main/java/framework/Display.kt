package framework

import java.awt.*
import java.lang.reflect.Field
import javax.swing.*
import kotlin.reflect.KMutableProperty
import kotlin.reflect.jvm.isAccessible

class Display(
    title: String,
    private val height: Int = 1000,
    private val width: Int = 1600,
    private val controlWidth: Int = 500,
) {
    val window: JFrame = JFrame(title)
    val canvas: Canvas
    private val watchedFields: WatchedFieldsPanel
    val controls: JPanel

    init {
        window.layout = BorderLayout()
        window.setPreferredSize(Dimension(width, height))
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
        window.pack()
        window.setLocationRelativeTo(null)
        window.isVisible = true

        canvas = Canvas()
        canvas.setPreferredSize(Dimension(width - controlWidth, height))
        window.add(canvas, BorderLayout.WEST)

        controls = ControlPanel(controlWidth, height)
        window.add(controls, BorderLayout.EAST)

        watchedFields = WatchedFieldsPanel(controlWidth - 50, height / 3)
        controls.addControl(watchedFields)
    }

    fun getHeight(): Int {
        return window.height
    }

    fun getWidth(): Int {
        return window.width
    }

    fun setupSlidersPanel(adjustableFields: Map<Any, Map<Field, Number>>) {
        adjustableFields.forEach { (obj, fields) ->
            val title = JLabel("Adjustable Fields for ${obj.javaClass.simpleName}:")
            watchedFields.add(title)

            fields.forEach { (field, currentValue) ->
                val annotation = field.getAnnotation(Watch::class.java)
                watchedFields.addField(
                    SliderPanel(
                        obj,
                        field,
                        annotation.name,
                        annotation.min,
                        annotation.max,
                        currentValue.toDouble(),
                        controlWidth - 60,
                        25
                    )
                )
            }
        }
    }

    private class SliderPanel(
        val obj: Any,
        val field: Field,
        displayName: String,
        min: Double,
        max: Double,
        currentValue: Number,
        width: Int,
        height: Int,
        color: Color = Color(230, 255, 255)
    ) : JPanel() {

        lateinit var currentValuePanel: JLabel
        init {
            preferredSize = Dimension(width, height)
            maximumSize = Dimension(width, height)
            minimumSize = Dimension(width, height)
            this.setBackground(color)
            val minInt = (min * 100).toInt()
            val maxInt = (max * 100).toInt()
            this.layout = BoxLayout(this, BoxLayout.X_AXIS)
            val slider = JSlider(JSlider.HORIZONTAL, minInt, maxInt, (currentValue.toDouble() * 100).toInt())
            slider.majorTickSpacing = (maxInt - minInt) / 100
            slider.paintTicks = false
            slider.paintLabels = false
            slider.addChangeListener {
                val newValue = slider.value / 100.0
                setAdjustableFieldValue(newValue)
                currentValuePanel.text = "$newValue"
            }
            currentValuePanel = JLabel("${slider.value / 100.0}")
            this.add(JLabel("$displayName: "))
            this.add(currentValuePanel)
            this.add(Box.createHorizontalGlue())
            this.add(JLabel("$min"))
            this.add(slider)
            this.add(JLabel("$max"))
        }

        private fun setAdjustableFieldValue(newValue: Double) {
            field.isAccessible = true
            field.set(obj, newValue)
            val memberProperty = obj::class.members.find { it.name == field.name }
            if (memberProperty is KMutableProperty<*>) {
                memberProperty.isAccessible = true
                memberProperty.setter.call(obj, newValue)
            }
        }
    }

    private class ControlPanel(width: Int, height: Int, color: Color = Color(126, 165, 213)) : JPanel() {
        private val controls = ArrayList<JPanel>()

        init {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            preferredSize = Dimension(width, height)
            maximumSize = Dimension(width, height)
            minimumSize = Dimension(width, height)
            setBackground(color)
        }

        fun addControl(controlPanel: JPanel) {
            controls.add(controlPanel)
            this.add(controlPanel)
            revalidate();
            repaint();
        }
    }

    private class WatchedFieldsPanel(width: Int, height: Int, color: Color = Color(230, 255, 255)) : JPanel() {
        val fields = ArrayList<SliderPanel>()
        init {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            preferredSize = Dimension(width, height)
            maximumSize = Dimension(width, height)
            minimumSize = Dimension(width, height)
            background = color
            add(JLabel("Watched Fields:"))
        }

        fun addField(sliderPanel: SliderPanel) {
            fields.add(sliderPanel)
            this.add(sliderPanel)
            revalidate()
            repaint()
        }
    }

}