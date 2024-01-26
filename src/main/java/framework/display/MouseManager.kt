package framework.display

import algebra.Vec2
import framework.Camera
import framework.Entity
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener

class MouseManager(val camera: Camera) : MouseMotionListener, MouseListener {
    private var mouseX: Int = -1
    private var mouseY: Int = -1
    private var lastHoveredEntity: Entity? = null

    fun tick() {
        if (mouseX < 0 || mouseX >= camera.screenWidth || mouseY < 0 || mouseY >= camera.screenHeight) return
        val entity: Entity? = camera.getEntityAt(mouseX, mouseY)
        entity?.outlineRasterization = true
        if (lastHoveredEntity != entity) {
            lastHoveredEntity?.outlineRasterization = false
            lastHoveredEntity = entity
        }
    }
    override fun mouseMoved(e: MouseEvent?) {
        if (e != null) {
            mouseX = e.x
            mouseY = e.y
        }
    }

    override fun mouseDragged(e: MouseEvent?) {
        TODO("Not yet implemented")
    }

    override fun mouseClicked(e: MouseEvent?) {
        TODO("Not yet implemented")
    }

    override fun mousePressed(e: MouseEvent?) {
        TODO("Not yet implemented")
    }

    override fun mouseReleased(e: MouseEvent?) {
        TODO("Not yet implemented")
    }

    override fun mouseEntered(e: MouseEvent?) {
        TODO("Not yet implemented")
    }

    override fun mouseExited(e: MouseEvent?) {
        TODO("Not yet implemented")
    }
}