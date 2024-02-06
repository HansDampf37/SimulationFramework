package physics

import algebra.Vec
import framework.Camera
import framework.Vertex
import framework.interfaces.Entity
import framework.interfaces.Status
import framework.times
import toVec
import java.awt.Color

class Plane(private val edges: List<Vec>): Entity {

    override var outlineRasterization: Boolean = false
    override var color: Vec? = Color.BLACK.toVec()
    override var position: Vec = edges[0]
    override var velocity: Vec = Vec.zero
    override var acceleration: Vec = Vec.zero
    override var status: Status = Status.Immovable

    override fun render(camera: Camera) {
        for (k in 0 .. 10) {
            val alpha = k / 10.0
            val p01 = alpha * edges[0] + (1 - alpha) * edges[1]
            val p02 = alpha * edges[0] + (1 - alpha) * edges[2]
            val p13 = alpha * edges[1] + (1 - alpha) * edges[3]
            val p23 = alpha * edges[2] + (1 - alpha) * edges[3]
            camera.renderLine(Vertex(p01, color!!, Vec.zero), Vertex(p23, color!!, Vec.zero), this)
            camera.renderLine(Vertex(p02, color!!, Vec.zero), Vertex(p13, color!!, Vec.zero), this)
        }
    }
}