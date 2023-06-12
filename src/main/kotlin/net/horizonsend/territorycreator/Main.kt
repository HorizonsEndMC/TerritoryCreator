package net.horizonsend.territorycreator

import org.bukkit.configuration.file.YamlConfiguration
import java.awt.image.BufferedImage
import java.io.File
import java.util.stream.Collectors
import javax.imageio.ImageIO

fun main(args: Array<String>) {
    val values: Array<String> = if (args.size > 0) {
        args
    } else {
        App.readInfoFromUser()
    }
    App.fileName = values[0]
    App.ymlName = values[1]
    val pathToImage = App.fileName
    println(pathToImage)
    var image: BufferedImage? = null
    try {
        image = ImageIO.read(File(pathToImage))
    } catch (var10: Exception) {
        var10.printStackTrace()
    }
    if (image != null) {
        println("image not null!")
        val creator = App(image)
        val territories: MutableList<Territory> = ArrayList()
        val configuration = YamlConfiguration.loadConfiguration(File(App.ymlName))
        val var7: Iterator<String> = configuration.getKeys(false).iterator()
        val dimensions = intArrayOf(0, 0)
        while (var7.hasNext()) {
            val id = var7.next()
            territories.add(
                Territory(
                    configuration.getStringList("$id.points").stream().map<Array<String>> { s: String ->
                        s.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    }.map<Point> { s: Array<String> ->
                        val x = s[0].toDouble().toInt()
                        val y = s[1].toDouble().toInt()
                        if (x > dimensions[0]) dimensions[0] = x
                        if (y > dimensions[1]) dimensions[1] = y
                        Point(x, y)
                    }
                        .collect(Collectors.toList()) as List<Point>, configuration.getString(
                        "$id.label"
                    )!!))
        }
        val width = image.width
        val height = image.height
        val sx = width / 800.0
        val sy = height / 800.0
        for (territory in territories) territory.points = ArrayList(territory.points.stream().map { p: Point ->
            Point(
                Math.round(p.x / sx).toInt(), Math.round(p.y / sy).toInt()
            )
        }
            .collect(Collectors.toList()) as Collection<Point>)
        creator.addTerritories(territories)
        creator.run()
    }
}
