package net.horizonsend.territorycreator

import java.awt.Color
import java.awt.Graphics
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent

class TextBox(var text: String, x: Int, y: Int, w: Int, h: Int) : ScreenObject(x, y, w, h) {
    var defaultText: String
    var focused = false

    init {
        defaultText = text
    }

    override fun onMouseClick(e: MouseEvent) {}
    override fun onFocusKeyTyped(e: KeyEvent) {
        if (e.keyChar == '\b') {
            if (text.length > 0) {
                println("Removing character!")
                println(text)
                text = text.substring(0, text.length - 1)
                println(text)
            }
        } else if (text === defaultText) {
            text = e.keyChar.toString() + ""
        } else {
            text += e.keyChar
        }
    }

    override fun onFocusGain() {
        focused = true
    }

    override fun onFocusLost() {
        focused = false
    }

    override fun draw(g: Graphics) {
        if (focused) {
            g.color = Color.WHITE
        } else {
            g.color = Color.GRAY
        }
        g.fillRect(x, y, w, h)
        g.color = Color.BLACK
        g.drawRect(x, y, w, h)
        g.drawString(text, x + 5, y + 15)
    }
}
