package net.emilla.action.box;

import android.content.ContentResolver;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import net.emilla.databinding.ListItemBinding;
import net.emilla.file.Files;
import net.emilla.file.ListItemHolder;

import java.util.Arrays;

public final class ListFileAdapter extends RecyclerView.Adapter<ListItemHolder> {

    private final LayoutInflater mInflater;

    @Nullable
    private Uri mFile;
    @Nullable
    private String[] mItems;

    public ListFileAdapter(LayoutInflater inflater) {
        mInflater = inflater;
    }

    @Override
    public ListItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        var binding = ListItemBinding.inflate(mInflater, parent, false);
        return new ListItemHolder(binding);
    }

    @Override
    public void onBindViewHolder(ListItemHolder holder, int position) {
        holder.labelView.setText(mItems[position]);
    }

    @Override
    public int getItemCount() {
        return mItems != null ? mItems.length : 0;
    }

    public boolean loadFile(ContentResolver cr, Uri file) {
        int oldItemCount = getItemCount();
        mItems = Files.nonBlankLines(cr, file);

        boolean success = mItems != null;
        if (!success) {
            file = null;
        }
        mFile = file;

        if (oldItemCount > 0) {
            notifyItemRangeRemoved(0, oldItemCount);
        }

        if (success) {
            int itemCount = mItems.length;
            if (itemCount > 0) {
                notifyItemRangeInserted(0, itemCount);
            }
        }

        return success;
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
        mItems[oldItemCount] = item;

        notifyItemInserted(oldItemCount);

        return TriResult.SUCCESS;
    }

    @Nullable
    public Uri file() {
        return mFile;
    }

}
