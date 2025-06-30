package net.emilla.struct.sort

sealed interface IndexPortion {
    companion object {
        @JvmStatic
        fun of(start: Int, last: Int): IndexPortion {
            return if (start == last) {
                Index(start)
            } else {
                IndexWindow(start, last)
            }
        }
    }
}
class Index internal constructor(val pos: Int) : IndexPortion
