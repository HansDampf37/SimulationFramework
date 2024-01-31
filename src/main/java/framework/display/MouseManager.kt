package framework.display

import algebra.Vec2
import algebra.Vec4
import framework.Camera
import framework.interfaces.Entity
import framework.interfaces.Status
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import javax.swing.SwingUtilities

class MouseManager(val camera: Camera) : MouseMotionListener, MouseListener {
    private val eventQueue: Queue<MouseEvent> = ConcurrentLinkedQueue()
    // hovering
    private var mouseX: Int = -1
    private var mouseY: Int = -1
    private var lastHoveredEntity: Entity? = null
    // dragging
    private var lastDragX: Int? = null
    private var lastDragY: Int? = null
    private var draggedEntity: Entity? = null
    private var distToDraggedObj: Double? = null
    private var previousMovementStatusOfDraggedObject: Status? = null
    // turning
    private var lastRotateX: Int? = null
    private var lastRotateY: Int? = null

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

    private fun onMouseClicked(e: MouseEvent) = Unit

    private fun onMousePressed(e: MouseEvent) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            // left button
            if (mouseX < 0 || mouseX >= camera.screenWidth || mouseY < 0 || mouseY >= camera.screenHeight) return
            draggedEntity = camera.getEntityAt(mouseX, mouseY)
            val entity = draggedEntity
            previousMovementStatusOfDraggedObject = entity?.status
            entity?.status = Status.Immovable
            if (entity != null) distToDraggedObj = camera.getDistanceToPointAt(entity.position)
            lastDragX = e.x
            lastDragY = e.y
        } else if (SwingUtilities.isRightMouseButton(e)) {
            // right button
            lastRotateX = e.x
            lastRotateY = e.y
        }
    }

    private fun onMouseDragged(e: MouseEvent, dt: Double) {
        if (SwingUtilities.isLeftMouseButton(e)) {
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
                ent.velocity = w * dt
                lastDragX = e.x
                lastDragY = e.y
            }
        } else if (SwingUtilities.isRightMouseButton(e)) {
            // right button
            val dx = e.x - lastRotateX!!
            val dy = e.y - lastRotateY!!
            lastRotateX = e.x
            lastRotateY = e.y
            camera.turn(Vec2(dx * dt, dy * dt))
        }
    }

    private fun onMouseReleased(e: MouseEvent) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            draggedEntity?.status = previousMovementStatusOfDraggedObject!!
            draggedEntity = null
            lastDragX = null
            lastDragY = null
            distToDraggedObj = null
        } else if (SwingUtilities.isRightMouseButton(e)) {
            // right button
            lastRotateX = null
            lastRotateY = null
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