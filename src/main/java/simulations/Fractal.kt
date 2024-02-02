package simulations

import framework.Simulation
import algebra.Point3d
import physics.Seconds
import java.awt.Color
import java.awt.Graphics
import kotlin.math.cos
import kotlin.math.sin

class Fractal(private val dim: Int) : Simulation("Fractal") {
    private lateinit var corners: Array<Point3d>
    private lateinit var points: MutableList<Point3d>

    init {
        reset()
    }

    override fun tick(dt: Seconds) {
        val speed = 5
        for (i in 0 until speed) {
            val corner = corners[(Math.random() * corners.size).toInt()]
            val newPoint =
                Point3d(points[points.size - 1].positionVector + points[points.size - 1].getConnectingVectorTo(corner) / 2)
            points.add(newPoint)
        }
    }

    override fun render() {
        TODO("Not yet implemented")
    }

    override fun reset() {
        val radius = 100.0
        points = ArrayList()
        corners = Array(dim) { i ->
            val phi = 2 * Math.PI / dim * i
            val x = radius * cos(phi + Math.PI / 2)
            val y = radius * sin(phi + Math.PI / 2)
            Point3d(x, y, 0.0)
        }
        // for (int i = 0; i < (int)((double)corners.length / 2.0 + 0.6); i++) corners[i] = new Point3d(Math.random() * scale * 2 - scale, Math.random() * scale * 2 - scale, 0);
        // for (int i = (int)((double)corners.length / 2.0 + 0.6); i < corners.length; i++) corners[i] = new Point3d(corners[i - corners.length / 2].getPositionVector().scale(-1));
        //corners[0] = new Point3d(0,-100,0);
        //corners[1] = new Point3d(-95,-31,0);
        //corners[2] = new Point3d(-59,81,0);
        //corners[3] = new Point3d(59,81,0);
        //corners[4] = new Point3d(95,-31,0);
        //corners[0] = new Point3d(0,-100,0);
        //corners[1] = new Point3d(-95,-31,0);
        //corners[2] = new Point3d(-59,81,0);
        points.add(Point3d(Math.random() * radius * 2 - radius, Math.random() * radius * 2 - radius, 0.0))
    }
}