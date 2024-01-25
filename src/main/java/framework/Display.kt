package framework

import java.awt.*
import java.lang.reflect.Field
import java.text.NumberFormat
import javax.swing.*
import kotlin.reflect.KMutableProperty


class Display(
    title: String,
    private val height: Int = 1000,
    private val width: Int = 1600,
    private val controlWidth: Int = 500,
) {
    val window: JFrame = JFrame(title)
    val canvas: Canvas
    private val watchedFields: WatchedFieldsPanel
    private val controls: ControlPanel

    init {
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

        watchedFields = WatchedFieldsPanel()
        controls.addControl(watchedFields)
        controls.add(Box.createGlue())
    }

    fun tick() {
        controls.tick()
    }

    fun getHeight(): Int {
        return window.height
    }

    fun getWidth(): Int {
        return window.width
    }

    fun setupSlidersPanel(adjustableFields: Map<Any, Map<Field, Number>>) {
        adjustableFields.forEach { (obj, fields) ->
            fields.forEach { (field, currentValue) ->
                val annotation = field.getAnnotation(Watch::class.java)
                watchedFields.addField(
                    WatchedFieldsPanel.FieldData(
                        annotation.name,
                        field,
                        obj,
                        annotation.min,
                        annotation.max,
                        currentValue.toDouble(),
                    )
                )
            }
        }
    }

    private class ControlPanel(width: Int, height: Int, color: Color = Color(126, 165, 213)) : JPanel() {
        private val controls = ArrayList<JPanel>()

        init {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            preferredSize = Dimension(width, height)
            setBackground(color)
        }

        fun addControl(controlPanel: JPanel) {
            controls.add(controlPanel)
            this.add(controlPanel)
            revalidate();
            repaint();
        }

        fun tick() {
            for (panel in controls) {
                (panel as WatchedFieldsPanel).tick()
            }
        }
    }

    private class WatchedFieldsPanel(val color: Color = Color(230, 255, 255)) : JPanel(GridBagLayout()) {
        val entries = ArrayList<Pair<FieldData, JSlider>>()

        fun tick() {
            for (entry in entries) {
                val current: Double = entry.first.field.get(entry.first.obj) as Double
                val slider: JSlider = entry.second
                slider.value = (current * 100).toInt()
            }
        }

        val c: GridBagConstraints = GridBagConstraints()

        init {
            c.gridy = 0
            c.gridx = 0
            background = color
            add(JLabel("Watched Fields:"), c)
            c.gridy = 1
            c.gridx = 0
            c.weightx = 0.5
            this.add(JLabel("Name"), c)
            c.weightx = 0.5
            c.gridx = 1
            this.add(JLabel("Wert"), c)
            c.gridx = 2
            c.weightx = 0.0
            this.add(JLabel("Minimum"), c)
            c.gridx = 4
            c.weightx = 0.0
            this.add(JLabel("Maximum"), c)
        }

        fun addField(fieldData: FieldData) {
            val minInt = (fieldData.min * 100).toInt()
            val maxInt = (fieldData.max * 100).toInt()
            val slider = JSlider(JSlider.HORIZONTAL, minInt, maxInt, (fieldData.current * 100).toInt())
            slider.setPreferredSize(Dimension(100, 25))
            slider.setMaximumSize(Dimension(100, 25))
            slider.setMinimumSize(Dimension(100, 25))
            slider.majorTickSpacing = (maxInt - minInt) / 100
            slider.paintTicks = false
            slider.paintLabels = false
            slider.background = color
            val currentValuePanel = JLabel(round(slider.value / 100.0))
            slider.addChangeListener {
                val newValue = slider.value / 100.0
                setAdjustableFieldValue(fieldData.field, fieldData.obj, newValue)
                currentValuePanel.text = "$newValue"
            }
            c.gridy = entries.size + 2
            c.gridx = 0
            c.weightx = 0.5
            this.add(JLabel(fieldData.displayName), c)
            c.weightx = 0.5
            c.gridx = 1
            this.add(currentValuePanel, c)
            c.gridx = 2
            c.weightx = 0.0
            this.add(JLabel(round(fieldData.min)), c)
            c.gridx = 3
            c.weightx = 0.0
            this.add(slider, c)
            c.gridx = 4
            c.weightx = 0.0
            this.add(JLabel(round(fieldData.max)), c)
            revalidate()
            repaint()
            entries.add(Pair(fieldData, slider))
        }

        private fun setAdjustableFieldValue(field: Field, obj: Any, newValue: Double) {
            field.isAccessible = true
            field.set(obj, newValue)
            val memberProperty = obj::class.members.find { it.name == field.name }
            if (memberProperty is KMutableProperty<*>) {
                memberProperty.setter.call(obj, newValue)
            }
        }

        class FieldData(val displayName: String, val field: Field, val obj: Any, val min: Double, val max: Double, var current: Double)
    }
    companion object {
        val nf: NumberFormat = NumberFormat.getNumberInstance().apply { maximumFractionDigits = 0 }
        fun round(number: Double) = nf.format(number)
    }
}

