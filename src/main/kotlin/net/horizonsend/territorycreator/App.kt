package net.horizonsend.territorycreator

import org.bukkit.configuration.file.YamlConfiguration
import java.awt.Color
import java.awt.Graphics
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.awt.image.BufferedImage
import java.awt.image.ImageObserver
import java.io.File
import java.util.Scanner
import java.util.stream.Collectors
import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.JFrame
import javax.swing.JOptionPane

class App(image: BufferedImage) : JFrame("Territory Creator v1.2.0"), Runnable, MouseListener,
    KeyListener {
    private val objects = ArrayList<ScreenObject>()
    private var focus: ScreenObject? = null
    private val nameBox: TextBox
    private val scaleXBox: TextBox
    private val scaleYBox: TextBox
    private val tlcornerX: TextBox
    private val tlcornerY: TextBox
    private val moveSpeed: TextBox
    private val tm: TerritoryMap
    private var leftPressed = false
    private var rightPressed = false
    private var upPressed = false
    private var downPressed = false

    init {
        var image = image
        defaultCloseOperation = 3
        val scaleX = image.width.toDouble()
        val scaleY = image.height.toDouble()
        background = Color.BLACK
        image = resize(image, 800, 800)
        instance = this
        setSize(1030, 800)
        tm = TerritoryMap(image)
        objects.add(tm)
        nameBox = TextBox("Enter Territory Name...", 810, 40, 200, 50)
        scaleXBox = TextBox(scaleX.toString() + "", 810, 100, 200, 50)
        scaleYBox = TextBox(scaleY.toString() + "", 810, 160, 200, 50)
        tlcornerX = TextBox("0", 810, 220, 200, 50)
        tlcornerY = TextBox("0", 810, 280, 200, 50)
        moveSpeed = TextBox("Enter Move Speed (default 5)...", 810, 340, 200, 50)
        objects.add(nameBox)
        objects.add(scaleXBox)
        objects.add(scaleYBox)
        objects.add(tlcornerX)
        objects.add(tlcornerY)
        objects.add(moveSpeed)
        val finishTerritory = Button("Finish Territory", {
            if (tm.enteredPoints.size > 0) {
                tm.finishTerritory(nameBox.text)
                nameBox.text = "Enter Territory Name..."
            }
            finish()
        }, 810, 400, 200, 50)
        objects.add(finishTerritory)
        val removeTerritory = Button("Remove Named Territory", {
            val name = nameBox.text
            if (name == "Enter Territory Name...") {
                errorPopup("No territory given to delete!")
            } else {
                tm.removeTerritory(name)
            }
        }, 810, 580, 200, 50)
        objects.add(removeTerritory)
        addMouseListener(this)
        addKeyListener(this)
        isVisible = true
        addWindowListener(object : WindowAdapter() {
            override fun windowClosed(e: WindowEvent) {
                System.exit(0)
            }
        })
        try {
            Thread.sleep(1000L)
        } catch (var9: InterruptedException) {
            var9.printStackTrace()
        }
    }

    private fun finish() {
        println("Exporting")
        if (scaleXBox.text == "Enter Map Scale X...") {
            errorPopup("Enter map scale X!")
        } else if (scaleYBox.text == "Enter Map Scale Y...") {
            errorPopup("Enter map scale Y!")
        } else if (tlcornerX.text == "Enter Top Left Corner X...") {
            errorPopup("Enter X coordinate for top left corner!")
        } else if (tlcornerY.text == "Enter Top Left Corner Y...") {
            errorPopup("Enter Y coordinate for top left corner!")
        } else {
            val tlx: Int
            val tly: Int
            var sx: Double
            var sy: Double
            try {
                tlx = tlcornerX.text.trim { it <= ' ' }.toInt()
                tly = tlcornerY.text.trim { it <= ' ' }.toInt()
                sx = scaleXBox.text.trim { it <= ' ' }.toDouble()
                sy = scaleYBox.text.trim { it <= ' ' }.toDouble()
            } catch (var25: Exception) {
                errorPopup("One of the numbers entered was not a number.")
                return
            }
            sx /= 800.0
            sy /= 800.0
            try {
                val f = File(ymlName)
                if (!f.exists()) f.createNewFile()
                val configuration = YamlConfiguration()
                val var9: Iterator<Territory> = tm.territories.iterator()
                while (var9.hasNext()) {
                    val t = var9.next()
                    t.print(configuration, tlx, tly, sx, sy)
                }
                configuration.save(f)
            } catch (var26: Exception) {
                var26.printStackTrace()
            }
            println("0, 0 is located at pixels: ")
            val rtlx = tlx / sx
            val rtly = tlx / sy
            val nx = (rtlx + 0.0) * sx
            val ny = (rtly + 0.0) * sy
            println("$nx,$ny")
            println("1000, 1000 is located at pixels: ")
            val rtlx2 = tlx / sx
            val rtly2 = tlx / sy
            val nx2 = (rtlx2 + 1000.0) * sx
            val ny2 = (rtly2 + 1000.0) * sy
            println("$nx2,$ny2")
            try {
                val f2 = File(fileName.substring(0, fileName.length - 4) + "-map.png")
                if (!f2.exists()) f2.createNewFile()
                ImageIO.write(tm.workingImage, "png", f2)
            } catch (var24: Exception) {
                var24.printStackTrace()
                JOptionPane.showMessageDialog(this, var24, "Error", 0)
            }
            JOptionPane.showMessageDialog(this, "Finished Exporting", "Done", 1)
        }
    }

    private fun errorPopup(message: String) {
        JOptionPane.showMessageDialog(this, message, "Error", 0)
    }

    internal fun addTerritories(terrs: List<Territory>) {
        tm.territories = ArrayList(terrs.stream().filter { t: Territory -> t.points.size > 0 }
            .collect(Collectors.toList()) as Collection<Territory>)
    }

    override fun run() {
        while (true) {
            var speed: Int
            try {
                Thread.sleep(33L)
            } catch (var5: InterruptedException) {
                var5.printStackTrace()
            }
            repaint()
            val text = moveSpeed.text
            speed = if (moveSpeed.text === moveSpeed.defaultText) {
                5
            } else {
                try {
                    text.toInt()
                } catch (var4: Exception) {
                    println("Speed value is not a number, please enter a number!")
                    5
                }
            }
            if (upPressed) tm.changeYDist(-1 * speed)
            if (downPressed) tm.changeYDist(speed)
            if (leftPressed) tm.changeXDist(-1 * speed)
            if (rightPressed) tm.changeXDist(speed)
        }
    }

    override fun paint(g: Graphics) {
        val image = BufferedImage(width, height, 1)
        val var3: Iterator<ScreenObject> = objects.iterator()
        while (var3.hasNext()) {
            val o = var3.next()
            o.draw(image.graphics)
        }
        g.drawImage(image, 100, 100, this)
    }

    override fun keyPressed(arg0: KeyEvent) {
        if (focus === tm) when (arg0.keyChar) {
            'a' -> {
                leftPressed = true
                return
            }

            'd' -> {
                rightPressed = true
                return
            }

            's' -> {
                downPressed = true
                return
            }

            'w' -> {
                upPressed = true
                return
            }
        }
    }

    override fun keyReleased(arg0: KeyEvent) {
        if (focus === tm) when (arg0.keyChar) {
            'a' -> {
                leftPressed = false
                return
            }

            'd' -> {
                rightPressed = false
                return
            }

            's' -> {
                downPressed = false
                return
            }

            'w' -> {
                upPressed = false
                return
            }
        }
    }

    override fun keyTyped(arg0: KeyEvent) {
        if (focus != null) focus!!.onFocusKeyTyped(arg0)
    }

    override fun mouseClicked(e: MouseEvent) {}
    override fun mousePressed(arg0: MouseEvent) {
        val p = Point(arg0.x - 100, arg0.y - 100)
        if (focus != null && focus!!.pointWithin(p)) {
            focus!!.onMouseClick(arg0)
        } else {
            if (focus != null) focus!!.onFocusLost()
            val var3: Iterator<ScreenObject> = objects.iterator()
            while (var3.hasNext()) {
                val o = var3.next()
                if (o.pointWithin(p)) {
                    focus = o
                    focus!!.onFocusGain()
                    focus!!.onMouseClick(arg0)
                    return
                }
            }
            focus = null
        }
    }

    override fun mouseReleased(arg0: MouseEvent) {}
    override fun mouseEntered(e: MouseEvent) {}
    override fun mouseExited(e: MouseEvent) {}
    private fun debug(value: String) {
        println(value)
    }

    companion object {
        private const val serialVersionUID = 1L
        lateinit var fileName: String
        lateinit var ymlName: String
        lateinit var instance: App
            private set

        private fun resize(img: BufferedImage, newW: Int, newH: Int): BufferedImage {
            val tmp = img.getScaledInstance(newW, newH, 4)
            val dimg = BufferedImage(newW, newH, 2)
            val g2d = dimg.createGraphics()
            g2d.drawImage(tmp, 0, 0, null as ImageObserver?)
            g2d.dispose()
            return dimg
        }



        fun readInfoFromUser(): Array<String> {
            val s = Scanner(System.`in`)
            val fc = JFileChooser()
            fc.currentDirectory = File(System.getProperty("user.dir"))
            fc.showDialog(null, "Select")
            val name = fc.selectedFile.path
            val terrfile = name.replace(".png", ".yml")
            s.close()
            return arrayOf(name, terrfile)
        }
    }
}