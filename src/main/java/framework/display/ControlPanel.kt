package framework.display

import framework.WatchedField
import java.awt.Component
import java.awt.Dimension
import javax.swing.BoxLayout
import javax.swing.JPanel
import javax.swing.border.EmptyBorder

class ControlPanel(width: Int, height: Int, padding: Int = 10) : JPanel() {
    private val watchedFieldsPanel: WatchedFieldsPanel = WatchedFieldsPanel(width - 2 * padding, height- 2 * padding)
    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        preferredSize = Dimension(width, height)
        maximumSize = Dimension(width, height)
        minimumSize = Dimension(width, height)
        border = EmptyBorder(padding, padding, padding, padding)

        // Set top alignment for each component added to the BoxLayout
        watchedFieldsPanel.setAlignmentY(Component.TOP_ALIGNMENT)

        add(watchedFieldsPanel)
    }


    fun setWatchedFields(watchedFields: Map<Any, List<WatchedField<*, *>>>) {
        watchedFieldsPanel.setWatchedFields(watchedFields)
        watchedFieldsPanel.revalidate()
        watchedFieldsPanel.repaint()
    }
}