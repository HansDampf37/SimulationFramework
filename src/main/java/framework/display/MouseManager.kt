package framework.display

import algebra.Vec4
import framework.Camera
import framework.interfaces.Entity
import framework.interfaces.Status
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
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
    private var previousMovementStatusOfDraggedObject: Status? = null

    private fun addEvent(e: MouseEvent) {
        eventQueue.add(e)
    }

    fun tick(dt: Double) {
        while(eventQueue.isNotEmpty()) {
            val event = eventQueue.poll()
            when (event.id) {
                MouseEvent.MOUSE_MOVED -> onMouseMoved(event)
                MouseEvent.MOUSE_CLICKED -> onMouseClicked(event)
                MouseEvent.MOUSE_PRESSED -> onMousePressed(event)
                MouseEvent.MOUSE_RELEASED -> onMouseReleased(event)
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

    private fun onMouseClicked(e: MouseEvent): Nothing = TODO("implement me")

    private fun onMouseReleased(e: MouseEvent) {
        draggedEntity?.status = previousMovementStatusOfDraggedObject!!
        draggedEntity = null
        lastDragX = null
        lastDragY = null
    }

    private fun onMousePressed(e: MouseEvent) {
        if (mouseX < 0 || mouseX >= camera.screenWidth || mouseY < 0 || mouseY >= camera.screenHeight) return
        draggedEntity = camera.getEntityAt(mouseX, mouseY)
        previousMovementStatusOfDraggedObject = draggedEntity?.status
        draggedEntity?.status = Status.Immovable
        lastDragX = e.x
        lastDragY = e.y
    }

    private fun onMouseDragged(e: MouseEvent, dt: Double) {
        val dx = e.x - lastDragX!!
        val dy = e.y - lastDragY!!
        val ent = draggedEntity
        if (ent != null) {
            val position = ent.position
            val cosAlpha = camera.lookingDirection * camera.getDirectionToPointAt(position)
            val z = cosAlpha * camera.getDistanceToPointAt(position)
            val s = camera.zoom / (camera.focalLength) * z
            val v = Vec4(dx.toDouble() * s, dy.toDouble() * s, 0.0, 1.0)
            val w = camera.rotateCameraToWorld * v
            ent.position += w
            ent.velocity += w * dt
            lastDragX = e.x
            lastDragY = e.y
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