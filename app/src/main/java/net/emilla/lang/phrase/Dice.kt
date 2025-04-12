package net.emilla.lang.phrase

import net.emilla.util.hash1
import java.util.Random

class Dice(count: Int, val faces: Int) : Comparable<Dice> {
    var count = count @JvmName("count") get
        private set

    fun add(count: Int) {
        this.count += count
    }

    fun roll(rand: Random): Int {
        if (faces == 1) return count

        var result = 0
        if (count >= 0) repeat(count) {
            result += rand.nextInt(faces) + 1
        } else repeat(count) {
            result -= rand.nextInt(faces) + 1
        }

        return result
    }

    override fun compareTo(that: Dice) = faces - that.faces
    override fun equals(that: Any?) = this === that || that is Dice && faces == that.faces
    override fun hashCode() = hash1(faces)
}
