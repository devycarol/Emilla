package net.emilla.action.box;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import net.emilla.annotation.internal;
import net.emilla.databinding.ChecklistItemBinding;
import net.emilla.file.ChecklistItemHolder;
import net.emilla.file.Files;
import net.emilla.file.ListItem;
import net.emilla.sort.IndexPortion;
import net.emilla.sort.SortedArrays;
import net.emilla.util.IntArrayLoader;

import java.util.Arrays;

final class ListFileAdapter extends RecyclerView.Adapter<ChecklistItemHolder> {

    private final Resources mResources;
    private final LayoutInflater mInflater;

    @Nullable
    private Uri mFile;
    @Nullable
    private ListItem[] mItems;

    private int mCheckedCount = 0;

    @internal ListFileAdapter(Resources res, LayoutInflater inflater) {
        mResources = res;
        mInflater = inflater;
    }

    @Override
    public ChecklistItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        var binding = ChecklistItemBinding.inflate(mInflater, parent, false);
        return new ChecklistItemHolder(binding);
    }

    @Override
    public void onBindViewHolder(ChecklistItemHolder holder, int position) {
        ListItem item = mItems[position];
        holder.label.setText(item.text);

        holder.setChecked(mResources, item.isChecked());
        holder.itemView.setOnClickListener(view -> onItemClick(holder));
    }

    private void onItemClick(ChecklistItemHolder holder) {
        int pos = holder.getBindingAdapterPosition();
        ListItem item = mItems[pos];
        boolean isChecked = item.toggle();
        holder.setChecked(mResources, isChecked);

        if (isChecked) {
            ++mCheckedCount;
        } else {
            --mCheckedCount;
        }
    }

    @Override
    public int getItemCount() {
        return mItems != null ? mItems.length : 0;
    }

    public boolean loadFile(ContentResolver cr, Uri file) {
        int oldItemCount = getItemCount();
        mItems = Files.textList(cr, file);

        if (oldItemCount > 0) {
            notifyItemRangeRemoved(0, oldItemCount);
        }

        boolean success = mItems != null;
        if (success) {
            mFile = file;

            int itemCount = mItems.length;
            if (itemCount > 0) {
                notifyItemRangeInserted(0, itemCount);
            }
        } else {
            mFile = null;
        }

        mCheckedCount = 0;

        return success;
    }

    private void unloadFile(int itemCount) {
        mFile = null;
        mItems = null;
        mCheckedCount = 0;
        notifyItemRangeRemoved(0, itemCount);
    }

    public TriResult addItem(ContentResolver cr, String item) {
        if (mItems == null || mFile == null) {
            return TriResult.WAITING;
        }

        if (!Files.appendLine(cr, mFile, item)) {
            return TriResult.FAILURE;
        }

        int oldItemCount = mItems.length;
        mItems = Arrays.copyOf(mItems, oldItemCount + 1);
        mItems[oldItemCount] = new ListItem(item);

        notifyItemInserted(oldItemCount);

        return TriResult.SUCCESS;
    }

    @Nullable
    public TriResult dismissSelection(ContentResolver cr) {
        if (mCheckedCount == 0) {
            return null;
        }

        if (mItems == null || mFile == null) {
            return TriResult.WAITING;
        }

        int itemCount = mItems.length;
        var selectionLoader = new IntArrayLoader(itemCount);
        var keepLoader = new IntArrayLoader(itemCount);

        for (int i = 0; i < itemCount; ++i) {
            if (mItems[i].isChecked()) {
                selectionLoader.add(i);
            } else {
                keepLoader.add(i);
            }
        }

        IndexPortion[] selection = SortedArrays.portions(selectionLoader.array());
        ListItem.rearrangeLeadingLines(mItems, selection);
        IndexPortion[] keep = SortedArrays.portions(keepLoader.array());
        mItems = SortedArrays.extract(mItems, keep);

        if (!Files.saveList(cr, mItems, mFile)) {
            unloadFile(itemCount);
            return TriResult.FAILURE;
        }

        for (int i = selection.length - 1; i >= 0; --i) {
            selection[i].removeFrom(this);
        }

        mCheckedCount = 0;

        return TriResult.SUCCESS;
    }

    @Nullable
    public Uri file() {
        return mFile;
    }

}
