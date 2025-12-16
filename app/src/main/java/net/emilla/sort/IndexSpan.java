package net.emilla.sort;

import androidx.recyclerview.widget.RecyclerView;

final class IndexSpan extends IndexPortion {

    private final int mLength;

    private IndexSpan(int index, int length) {
        super(index);

        mLength = length;
    }

    /*internal*/ static IndexSpan window(int start, int end) {
        return new IndexSpan(start, end - start);
    }

    @Override
    public int nextIndex() {
        return this.index + mLength;
    }

    @Override
    public void removeFrom(RecyclerView.Adapter<?> adapter) {
        adapter.notifyItemRangeRemoved(this.index, mLength);
    }

    @Override
    public <E> int shiftTo(E[] array, int position) {
        System.arraycopy(array, this.index, array, position, mLength);
        return position + mLength;
    }

}
