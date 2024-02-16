package framework.interfaces

import algebra.Vec3BLablabla
import framework.Camera
import framework.Vertex

/**
 * A Volume is an [Entity] that has a **convex** 3d-shape which is defined by [meshObjCoords]. These cords are defined
 * in the Object coordinate system. Depending on the [position] and the angles [yaw], [pitch], and [roll] these coords
 * will be mapped to world coordinates by the function [meshToWorldCoordinates].
 *
 * Zero in object coordinates should be mapped to [position] in world coordinates. [position] is interpreted as centre-point.
 * So the convex shape should contain zero in object coordinates and therefor [position] in world coordinates.
 *
 * The meshes [meshObjCoords] and [meshToWorldCoordinates] will be interpreted as triangle meshes.
 * Each subsequent triplet of vectors in this list will be interpreted as one triangle to draw.
 */
interface Volume: Entity {
    /**
     * The mesh of the volume in the object coordinate system
     */
    val meshObjCoords: List<Vec3BLablabla>
    var yaw: Double
    var pitch: Double
    var roll: Double

    /**
     * Maps the mesh to the world coordinate system
     */
    fun meshToWorldCoordinates(): List<Vertex>

    override fun render(camera: Camera) {
        camera.renderStrip(meshToWorldCoordinates(), this)
    }
}