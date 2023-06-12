package net.horizonsend.territorycreator

import java.awt.Color
import java.awt.Graphics
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent

class Button(var text: String, var execute: Runnable, x: Int, y: Int, w: Int, h: Int) : ScreenObject(x, y, w, h) {
    override fun onMouseClick(e: MouseEvent) {
        execute.run()
    }

    override fun onFocusKeyTyped(e: KeyEvent) {}
    override fun onFocusGain() {}
    override fun onFocusLost() {}
    override fun draw(g: Graphics) {
        g.color = Color.DARK_GRAY
        g.fillRect(x, y, w, h)
        g.color = Color.BLACK
        g.drawRect(x, y, w, h)
        g.drawString(text, x + 5, y + 15)
    }
}
