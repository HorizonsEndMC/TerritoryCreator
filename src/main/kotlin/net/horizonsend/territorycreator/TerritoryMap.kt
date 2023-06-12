package net.horizonsend.territorycreator

import java.awt.Color
import java.awt.Graphics
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage

class TerritoryMap internal constructor(image: BufferedImage) : ScreenObject(0, 0, 800, 800) {
    var enteredPoints = ArrayList<Point>()
    var workingImage: BufferedImage
    internal var territories = ArrayList<Territory>()
    private val baseImage: BufferedImage
    private var lastPoint: Point? = null
    private var enteredPointsColor: Color
    private var xdist = 0
    private var ydist = 0

    init {
        enteredPointsColor = Color.RED
        workingImage = deepCopy(image)
        baseImage = deepCopy(image)
    }

    private fun roundToMultiple(number: Int): Int {
        val remainder = (number % 10).toFloat()
        var base = number - remainder
        if (remainder >= 5.0f) base += 10.0f
        println("$number -> $base")
        return base.toInt()
    }

    override fun onMouseClick(paramMouseEvent: MouseEvent) {
        if (paramMouseEvent.button == 1) {
            val coords = Point(roundToMultiple(paramMouseEvent.x + xdist) - 100, roundToMultiple(paramMouseEvent.y + ydist) - 100)
            val var3: Iterator<Point> = enteredPoints.iterator()
            while (var3.hasNext()) {
                val p = var3.next()
                if (p.isWithinRadiusOf(10, coords)) {
                    if (lastPoint !== p) {
                        enteredPoints.add(p)
                        lastPoint = p
                        enteredPointsColor = Color.GREEN
                    } else {
                        println("Last point is equal to current point, not placing point")
                    }
                    return
                }
            }
            for (t in territories) {
                for (p in t.points) {
                    if (p.isWithinRadiusOf(10, coords)) {
                        enteredPoints.add(p)
                        lastPoint = p
                        return
                    }
                }
            }
            enteredPoints.add(coords)
            lastPoint = coords
        } else if (paramMouseEvent.button == 3 && enteredPoints.size > 0) {
            enteredPoints.remove(enteredPoints[enteredPoints.size - 1])
            if (enteredPoints.size > 0) {
                lastPoint = enteredPoints[enteredPoints.size - 1]
            } else {
                lastPoint = null
            }
            if (enteredPointsColor === Color.GREEN) enteredPointsColor = Color.RED
        }
    }

    fun removeTerritory(name: String) {
        for (i in territories.indices) {
            val t = territories[i]
            if (t.name == name) {
                territories.removeAt(i)
                return
            }
        }
    }

    override fun onFocusKeyTyped(paramKeyEvent: KeyEvent) {}
    override fun onFocusGain() {}
    override fun onFocusLost() {}
    override fun draw(paramGraphics: Graphics) {
        val g = workingImage.graphics
        g.drawImage(baseImage, 0, 0, App.instance)
        for (t in territories) {
            t.draw(g)
        }
        g.color = enteredPointsColor
        for (i in 0 until enteredPoints.size - 1) {
            val p = enteredPoints[i]
            val finish = enteredPoints[i + 1]
            g.drawLine(p.x, p.y, finish.x, finish.y)
        }
        g.color = Color.BLACK
        for (p in enteredPoints) {
            g.drawRect(p.x - 1, p.y - 1, 3, 3)
        }
        paramGraphics.color = Color.LIGHT_GRAY
        paramGraphics.drawRect(0, 0, 799, 799)
        if (workingImage.width > 800 && workingImage.height > 800) {
            paramGraphics.drawImage(workingImage.getSubimage(xdist, ydist, 799, 799), 0, 0, App.instance)
        } else if (workingImage.width > 800) {
            paramGraphics.drawImage(
                workingImage.getSubimage(xdist, ydist, 799, workingImage.height - 1),
                0,
                0,
                App.instance
            )
        } else if (workingImage.height > 800) {
            paramGraphics.drawImage(
                workingImage.getSubimage(xdist, ydist, workingImage.width, 799),
                0,
                0,
                App.instance
            )
        } else {
            paramGraphics.drawImage(workingImage, 0, 0, App.instance)
        }
    }

    fun finishTerritory(name: String?) {
        if (name != null && name != "" && name != "Enter Territory Name...") {
            if (enteredPointsColor === Color.GREEN) {
                territories.add(Territory(enteredPoints, name))
                enteredPointsColor = Color.RED
                enteredPoints.clear()
            } else {
                println("Territory is not closed! Close it please!")
            }
        } else {
            println("Name was not entered! Please enter a name!")
        }
    }

    fun changeXDist(change: Int) {
        if (baseImage.width > 800) {
            xdist += change
            if (xdist < 0) xdist = 0
            if (xdist > baseImage.width - 800) xdist = baseImage.width - 800
        }
    }

    fun changeYDist(change: Int) {
        if (baseImage.height > 800) {
            ydist += change
            if (ydist < 0) ydist = 0
            if (ydist > baseImage.height - 800) ydist = baseImage.height - 800
        }
    }

    companion object {
        private fun deepCopy(bi: BufferedImage): BufferedImage {
            val cm = bi.colorModel
            val isAlphaPremultiplied = cm.isAlphaPremultiplied
            val raster = bi.copyData(null)
            return BufferedImage(cm, raster, isAlphaPremultiplied, null).getSubimage(0, 0, bi.width, bi.height)
        }
    }
}
