package net.emilla.sort;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import net.emilla.databinding.ListItemBinding;
import net.emilla.file.ListItemHolder;
import net.emilla.struct.IndexedStruct;

import java.util.function.Consumer;
import java.util.function.IntFunction;

public /*open*/ class ItemSearchAdapter<E extends SearchItem>
    extends RecyclerView.Adapter<ListItemHolder> {

    private final LayoutInflater mInflater;

    protected final ArraySearcher<E> searcher;
    private IndexedStruct<E> mSearchResult;

    private final Consumer<E> mItemClickAction;

    @Nullable
    private String mSearch = null;

    public ItemSearchAdapter(
        LayoutInflater inflater,
        E[] items,
        Consumer<E> itemClickAction,
        IntFunction<E[]> arrayGenerator
    ) {
        mInflater = inflater;

        this.searcher = new ArraySearcher<E>(items, arrayGenerator);
        mSearchResult = this.searcher.search(null);

        mItemClickAction = itemClickAction;
    }

    public final void search(@Nullable String search) {
        searchInternal(search);
        mSearch = search;
    }

    private void searchInternal(@Nullable String search) {
        mSearchResult = this.searcher.search(search);
        notifyDataSetChanged();
    }

    protected final void refresh() {
        searchInternal(mSearch);
    }

    public final void add(E item) {
        this.searcher.add(item);
        refresh();
    }

    public final void remove(E item) {
        this.searcher.remove(item);
        refresh();
    }

    @Nullable
    public final E preferredItem(String search) {
        if (getItemCount() == 1) {
            return itemAt(0);
        }

        if (mSearchResult instanceof SearchResult<E> result) {
            ArrayWindow<E> primaryItems = result.mPrimaryItems;
            if (primaryItems.size() == 1) {
                return primaryItems.get(0);
            }
        }

        ArrayWindow<E> nameMatches = this.searcher.itemsNamed(search);
        if (nameMatches.size() == 1) {
            return nameMatches.get(0);
        }

        return exactItem(nameMatches, search);
    }

    @Nullable
    public final E exactItem(String search) {
        return exactItem(this.searcher.itemsNamed(search), search);
    }

    @Nullable
    private E exactItem(Iterable<E> items, String search) {
        for (E item : items) {
            if (search.equals(item.displayName)) {
                // case sensitive
                return item;
            }
        }

        return null;
    }

    @Override
    public final ListItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        var binding = ListItemBinding.inflate(mInflater, parent, false);
        return new ListItemHolder(binding);
    }

    @Override
    public final void onBindViewHolder(ListItemHolder holder, int pos) {
        holder.itemView.setOnClickListener(v -> {
            mItemClickAction.accept(itemAt(holder.getBindingAdapterPosition()));
        });
        holder.labelView.setText(itemAt(pos).displayName);
    }

    private E itemAt(int index) {
        return mSearchResult.get(index);
    }

    @Override
    public final int getItemCount() {
        return mSearchResult.size();
    }

}
