package net.emilla.action.box;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.emilla.action.box.SnippetAdapter.SnippetHolder;
import net.emilla.databinding.SnippetItemBinding;
import net.emilla.util.SortedArray;

/*internal*/ final class SnippetAdapter extends RecyclerView.Adapter<SnippetHolder> {

    private final SortedArray<String> mLabels;
    private OnItemClickListener mItemClickAction;

    public SnippetAdapter(SortedArray<String> labels, OnItemClickListener itemClickAction) {
        mLabels = labels;
        mItemClickAction = itemClickAction;
    }

    public void setItemClickAction(OnItemClickListener itemClickAction) {
        mItemClickAction = itemClickAction;
    }

    @Override @NonNull
    public SnippetHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var ctx = parent.getContext();
        var inflater = LayoutInflater.from(ctx);
        var binding = SnippetItemBinding.inflate(inflater, parent, false);
        return new SnippetHolder(binding);
    }

    @Override
    public void onBindViewHolder(SnippetHolder holder, int pos) {
        holder.labelView.setText(mLabels.get(pos));
        holder.itemView.setOnClickListener(v -> mItemClickAction.onClick(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return mLabels.size();
    }

    public static final class SnippetHolder extends RecyclerView.ViewHolder {

        private final TextView labelView;

        public SnippetHolder(SnippetItemBinding binding) {
            super(binding.getRoot());
            labelView = binding.snippetLabel;
        }
    }

    @FunctionalInterface
    public interface OnItemClickListener {

        void onClick(int pos);
    }
}
