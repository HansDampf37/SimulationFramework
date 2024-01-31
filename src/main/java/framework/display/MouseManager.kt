package framework.display

import algebra.Vec4
import framework.Camera
import framework.interfaces.Entity
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

class MouseManager(val camera: Camera) : MouseMotionListener, MouseListener {
    private val eventQueue: Queue<MouseEvent> = ConcurrentLinkedQueue()
    private var mouseX: Int = -1
    private var mouseY: Int = -1
    private var lastDragX: Int? = null
    private var lastDragY: Int? = null
    private var lastHoveredEntity: Entity? = null
    private var draggedEntity: Entity? = null
    private var distToDraggedObj: Double? = null

    private fun addEvent(e: MouseEvent) {
        eventQueue.add(e)
    }

    fun tick(dt: Double) {
        while(eventQueue.isNotEmpty()) {
            val event = eventQueue.poll()
            when (event.id) {
                MouseEvent.MOUSE_MOVED -> onMouseMoved(event)
                MouseEvent.MOUSE_CLICKED -> onMouseClicked()
                MouseEvent.MOUSE_PRESSED -> onMousePressed(event)
                MouseEvent.MOUSE_RELEASED -> onMouseReleased()
                MouseEvent.MOUSE_DRAGGED -> onMouseDragged(event, dt)
                MouseEvent.MOUSE_ENTERED -> Unit
                MouseEvent.MOUSE_EXITED -> Unit
                else -> TODO("${event.id} not implemented")
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

    private fun onMouseClicked() = Unit

    private fun onMouseReleased() {
        draggedEntity = null
        lastDragX = null
        lastDragY = null
        distToDraggedObj = null
    }

    private fun onMousePressed(e: MouseEvent) {
        if (mouseX < 0 || mouseX >= camera.screenWidth || mouseY < 0 || mouseY >= camera.screenHeight) return
        val ent = camera.getEntityAt(mouseX, mouseY)
        lastDragX = e.x
        lastDragY = e.y
        distToDraggedObj = if (ent != null) camera.getDistanceToPointAt(ent.position) else null
        draggedEntity = ent
    }

    private fun onMouseDragged(e: MouseEvent, dt: Double) {
        val dx = e.x - lastDragX!!
        val dy = e.y - lastDragY!!
        val ent = draggedEntity
        val dist = distToDraggedObj
        if (ent != null && dist != null) {
            val position = ent.position
            val cosAlpha = camera.lookingDirection * camera.getDirectionToPointAt(position)
            val z = cosAlpha * dist
            val s = camera.zoom / (camera.focalLength) * z
            val v = Vec4(dx.toDouble() * s, dy.toDouble() * s, 0.0, 1.0)
            val w = camera.rotateCameraToWorld * v
            ent.position += w
            ent.velocity += w * dt
            lastDragX = e.x
            lastDragY = e.y
        } else {
            throw IllegalStateException("Variables should not be null")
        }
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