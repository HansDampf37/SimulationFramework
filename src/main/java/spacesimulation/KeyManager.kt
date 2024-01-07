package spacesimulation

import java.awt.event.KeyEvent
import java.awt.event.KeyListener

class KeyManager : KeyListener {
    private val pressed = BooleanArray(1000)
    var w = false
    var s = false
    var a = false
    var d = false
    var y = false
    var n = false
    var out = false
    var space = false
    var f = false
    var g = false
    var v = false
    var b = false
    var up = false
    var down = false
    var left = false
    var right = false
    fun tick() {
        w = pressed[KeyEvent.VK_W]
        s = pressed[KeyEvent.VK_S]
        a = pressed[KeyEvent.VK_A]
        d = pressed[KeyEvent.VK_D]
        y = pressed[KeyEvent.VK_Y]
        n = pressed[KeyEvent.VK_N]
        out = pressed[KeyEvent.VK_X]
        space = pressed[KeyEvent.VK_SPACE]
        f = pressed[KeyEvent.VK_F]
        g = pressed[KeyEvent.VK_G]
        v = pressed[KeyEvent.VK_V]
        b = pressed[KeyEvent.VK_B]
        up = pressed[KeyEvent.VK_UP]
        down = pressed[KeyEvent.VK_DOWN]
        left = pressed[KeyEvent.VK_LEFT]
        right = pressed[KeyEvent.VK_RIGHT]
    }

    override fun keyTyped(e: KeyEvent) {}
    override fun keyPressed(e: KeyEvent) {
        pressed[e.keyCode] = true
    }

    override fun keyReleased(e: KeyEvent) {
        pressed[e.keyCode] = false
    }
}