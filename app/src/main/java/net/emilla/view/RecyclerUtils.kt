@file:JvmName("RecyclerUtils")

package net.emilla.view

import androidx.recyclerview.widget.RecyclerView
import net.emilla.struct.sort.IndexWindow

fun RecyclerView.Adapter<*>.notifyFrontPadded(count: Int) {
    if (count > 0) notifyItemRangeInserted(0, count)
}

fun RecyclerView.Adapter<*>.notifyItemSpanInserted(start: Int, end: Int) {
    if (start < end) notifyItemRangeInserted(start, end - start)
}

fun RecyclerView.Adapter<*>.notifyTrimmedUntil(pos: Int) {
    if (pos > 0) notifyItemRangeRemoved(0, pos)
}

fun RecyclerView.Adapter<*>.notifyItemSpanRemoved(start: Int, end: Int) {
    if (start < end) notifyItemRangeRemoved(start, end - start)
}

fun RecyclerView.Adapter<*>.notifyItemsClamped(window: IndexWindow, oldSize: Int) {
    notifyItemSpanRemoved(window.end, oldSize)
    notifyTrimmedUntil(window.start)
}
