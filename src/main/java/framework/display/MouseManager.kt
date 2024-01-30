package framework.display

import algebra.Vec4
import framework.Camera
import framework.Entity
import physics.Sphere
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.lang.reflect.Array
import java.util.*

class MouseManager(val camera: Camera) : MouseMotionListener, MouseListener {
    private val eventQueue: MutableList<MouseEvent> = ArrayList()
    private var mouseX: Int = -1
    private var mouseY: Int = -1
    private var lastDragX: Int? = null
    private var lastDragY: Int? = null
    private var lastHoveredEntity: Entity? = null
    private var draggedEntity: Entity? = null

    @Synchronized private fun addEvent(e: MouseEvent) {
        eventQueue.add(e)
    }

    fun tick(dt: Double) {
        while(eventQueue.isNotEmpty()) {
            val event = eventQueue.removeFirst()
            when (event.id) {
                MouseEvent.MOUSE_MOVED -> onMouseMoved(event)
                MouseEvent.MOUSE_CLICKED -> onMouseClicked(event)
                MouseEvent.MOUSE_PRESSED -> onMousePressed(event)
                MouseEvent.MOUSE_RELEASED -> onMouseReleased(event)
                MouseEvent.MOUSE_DRAGGED -> onMouseDragged(event, dt)
                MouseEvent.MOUSE_ENTERED -> Unit
                MouseEvent.MOUSE_EXITED -> Unit
                else -> TODO("Not implemented")
            }
        }
    }

    private fun onMouseMoved(e: MouseEvent) {
        mouseX = e.x
        mouseY = e.y
        if (mouseX < 0 || mouseX >= camera.screenWidth || mouseY < 0 || mouseY >= camera.screenHeight) return
        val hoveredEntity = camera.getEntityAt(mouseX, mouseY)
        if (hoveredEntity != lastHoveredEntity) {
            lastHoveredEntity?.outlineRasterization = false
            hoveredEntity?.outlineRasterization = true
            lastHoveredEntity = hoveredEntity
        }
    }

    private fun onMouseClicked(e: MouseEvent) = Unit

    private fun onMouseReleased(e: MouseEvent) {
        draggedEntity = null
        lastDragX = null
        lastDragY = null
    }

    private fun onMousePressed(e: MouseEvent) {
        if (mouseX < 0 || mouseX >= camera.screenWidth || mouseY < 0 || mouseY >= camera.screenHeight) return
        draggedEntity = camera.getEntityAt(mouseX, mouseY)
        lastDragX = e.x
        lastDragY = e.y
    }

    private fun onMouseDragged(e: MouseEvent, dt: Double) {
        val dx = e.x - lastDragX!!
        val dy = e.y - lastDragY!!
        val s = camera.zoom / (camera.getDistanceTo(draggedEntity as Sphere) * camera.focalLength)
        val v = Vec4(dx.toDouble() * s, dy.toDouble() * s, 0.0, 1.0)
        val w = camera.rotateCameraToWorld * v
        (draggedEntity as Sphere).set((draggedEntity as Sphere).positionVector - w)
        lastDragX = e.x
        lastDragY = e.y
    }

    //----------------------------------------------------------------------------------
    override fun mouseMoved(e: MouseEvent?) = if (e!=null) addEvent(e) else Unit

    override fun mouseDragged(e: MouseEvent?) = if (e!=null) addEvent(e) else Unit

    override fun mouseClicked(e: MouseEvent?) {
        e?.component?.requestFocus()
        if (e!=null) addEvent(e) else Unit
    }

    override fun mousePressed(e: MouseEvent?) = if (e!=null) addEvent(e) else Unit

    override fun mouseReleased(e: MouseEvent?) = if (e!=null) addEvent(e) else Unit

    override fun mouseEntered(e: MouseEvent?) = if (e!=null) addEvent(e) else Unit

    override fun mouseExited(e: MouseEvent?) = if (e!=null) addEvent(e) else Unit
}