package net.horizonsend.territorycreator

import java.awt.Graphics
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent

abstract class ScreenObject(var x: Int, var y: Int, var w: Int, var h: Int) {

    fun pointWithin(p: Point): Boolean {
        return x <= p.x && p.x <= x + w && y <= p.y && p.y <= y + h
    }

    abstract fun onMouseClick(paramMouseEvent: MouseEvent)
    abstract fun onFocusKeyTyped(paramKeyEvent: KeyEvent)
    abstract fun onFocusGain()
    abstract fun onFocusLost()
    abstract fun draw(paramGraphics: Graphics)
}
