package net.horizonsend.territorycreator

import org.bukkit.configuration.file.YamlConfiguration
import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.Polygon
import java.util.Locale

internal class Territory(points: List<Point>, name: String) {
    var points = ArrayList<Point>()
    var name: String

    init {
        val var3 = points.iterator()
        while (var3.hasNext()) {
            val p = var3.next()
            this.points.add(Point(p.x, p.y))
        }
        this.name = name
    }

    fun draw(g: Graphics) {
        g.color = Color(Color.PINK.red, Color.PINK.green, Color.PINK.blue, 100)
        val x = points.stream().map { point: Point -> Integer.valueOf(point.x) }
            .mapToInt { i: Int? -> i!! }.toArray()
        val y = points.stream().map { point: Point -> Integer.valueOf(point.y) }
            .mapToInt { i: Int? -> i!! }.toArray()
        val polygon = Polygon(x, y, x.size)
        g.fillPolygon(polygon)
        g.color = Color.PINK
        for (i in 0 until points.size - 1) {
            val p = points[i]
            val finish = points[i + 1]
            g.drawLine(p.x, p.y, finish.x, finish.y)
        }
        g.color = Color.BLACK
        val var5: Iterator<Point> = points.iterator()
        while (var5.hasNext()) {
            val p = var5.next()
            g.drawRect(p.x - 1, p.y - 1, 3, 3)
        }
        g.color = Color(255, 255, 255, 150)
        val centerX = polygon.bounds.bounds2D.centerX.toInt()
        val centerY = polygon.bounds.bounds2D.centerY.toInt()
        g.font = Font("TimesRoman", 1, 10)
        val metrics = g.getFontMetrics(g.font)
        g.drawString(name, centerX - metrics.stringWidth(name) / 2, centerY)
    }

    fun print(out: YamlConfiguration, tlx: Int, tly: Int, sx: Double, sy: Double) {
        val id = name.lowercase(Locale.getDefault()).replace(" ", "_").replace(")", "").replace("(", "")
        out["$id.label"] = name
        val pointList: MutableList<String> = ArrayList()
        val var10: Iterator<Point> = points.iterator()
        while (var10.hasNext()) {
            val p = var10.next()
            val x = Math.round(tlx / sx + p.x * sx)
            val y = Math.round(tly / sy + p.y * sy)
            pointList.add("$x $y")
        }
        out["$id.points"] = pointList
    }
}
