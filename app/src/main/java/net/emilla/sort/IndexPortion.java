package net.emilla.sort;

import androidx.recyclerview.widget.RecyclerView;

import net.emilla.annotation.internal;
import net.emilla.annotation.open;

public sealed class IndexPortion permits IndexSpan {

    public final int index;

    @internal IndexPortion(int index) {
        this.index = index;
    }

    public @open int nextIndex() {
        return this.index + 1;
    }

    public @open void removeFrom(RecyclerView.Adapter<?> adapter) {
        adapter.notifyItemRemoved(this.index);
    }

    public @open <E> int shiftTo(E[] array, int position) {
        array[position] = array[this.index];
        return position + 1;
    }

}
