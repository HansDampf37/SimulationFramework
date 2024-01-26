package framework.display

import framework.WatchedField
import java.awt.Color
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.lang.Thread.sleep
import java.util.concurrent.locks.ReentrantLock
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants
import javax.swing.border.EmptyBorder
import kotlin.concurrent.withLock

class WatchedFieldsPanel(width: Int, height: Int, padding: Int = 30) : JPanel(GridBagLayout()) {
    private val entries = ArrayList<WatchedField<*, *, *>>()

    init {
        Thread {
            while (true) {
                synchronized(entries) {
                    for (entry in entries) {
                        entry.updateControlComponent()
                    }
                    sleep(60)
                }
            }
        }.start()
        preferredSize = Dimension(width, height)
        maximumSize = Dimension(width, height)
        minimumSize = Dimension(width, height)
        border = EmptyBorder(padding, padding, padding, padding)
    }

    fun setWatchedFields(watchedFields: Map<Any, List<WatchedField<*, *, *>>>) {
        val c = GridBagConstraints()
        c.anchor = GridBagConstraints.WEST
        var y = 0
        c.gridy = y++
        c.gridx = 0
        c.gridwidth = 3
        add(JLabel("Watched Fields:").apply { font = Display.titleFont }, c)
        c.gridwidth = 1

        c.gridy = y
        c.gridx = 0
        c.weightx = 0.0
        this.add(JLabel("Name").apply { font = Display.columnNameFont }, c)
        c.gridy = y++
        c.gridx = 1
        this.add(JLabel("Wert").apply { font = Display.columnNameFont }, c)
        for ((obj, fields) in watchedFields) {
            c.gridy = y++
            c.gridx = 0
            c.gridwidth = 3
            add(JLabel(obj::class.simpleName + " " + obj.toString(), SwingConstants.LEFT).apply { font = Display.subsectionFont; background = Color.MAGENTA; horizontalAlignment = SwingConstants.LEFT }, c)
            c.gridwidth = 1
            entries.addAll(fields)
            for (field in fields) {
                c.gridy = y++
                c.gridx = 0
                c.weightx = 1.0
                this.add(JLabel(field.displayName).apply { font = Display.entryFont }, c)
                c.gridx = 1
                c.weightx = 1.0
                this.add(field.displayComponent.apply { font = Display.entryFont }, c)
                c.gridx = 2
                c.weightx = 1.0
                this.add(field.controlComponent, c)
            }
        }
    }
}