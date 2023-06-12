package net.horizonsend.territorycreator

class Point(@JvmField var x: Int, @JvmField var y: Int) {

    override fun toString(): String {
        return x.toString() + "," + y
    }

    fun isWithinRadiusOf(radius: Int, other: Point): Boolean {
        val dx = x - other.x
        val dy = y - other.y
        return radius * radius >= dx * dx + dy * dy
    }

    fun clone(): Point {
        return Point(x, y)
    }
}
