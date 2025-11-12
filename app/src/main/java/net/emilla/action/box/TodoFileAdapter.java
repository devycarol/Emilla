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

public final class TodoFileAdapter extends RecyclerView.Adapter<ListItemHolder> {

    private final LayoutInflater mInflater;

    @Nullable
    private Uri mFile;
    @Nullable
    private String[] mTasks;

    public TodoFileAdapter(LayoutInflater inflater) {
        mInflater = inflater;
    }

    @Override
    public ListItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        var binding = ListItemBinding.inflate(mInflater, parent, false);
        return new ListItemHolder(binding);
    }

    @Override
    public void onBindViewHolder(ListItemHolder holder, int position) {
        holder.labelView.setText(mTasks[position]);
    }

    @Override
    public int getItemCount() {
        return mTasks != null ? mTasks.length : 0;
    }

    public boolean loadFile(ContentResolver cr, Uri file) {
        int oldTaskCount = getItemCount();
        mTasks = Files.nonBlankLines(cr, file);

        boolean success = mTasks != null;
        if (!success) {
            file = null;
        }
        mFile = file;

        if (oldTaskCount > 0) {
            notifyItemRangeRemoved(0, oldTaskCount);
        }

        if (success) {
            int taskCount = mTasks.length;
            if (taskCount > 0) {
                notifyItemRangeInserted(0, taskCount);
            }
        }

        return success;
    }

    public TriResult addTask(ContentResolver cr, String task) {
        if (mTasks == null || mFile == null) {
            return TriResult.WAITING;
        }

        if (!Files.appendLine(cr, mFile, task)) {
            return TriResult.FAILURE;
        }

        int oldTaskCount = mTasks.length;
        mTasks = Arrays.copyOf(mTasks, oldTaskCount + 1);
        mTasks[oldTaskCount] = task;

        notifyItemInserted(oldTaskCount);

        return TriResult.SUCCESS;
    }

    @Nullable
    public Uri file() {
        return mFile;
    }

}
