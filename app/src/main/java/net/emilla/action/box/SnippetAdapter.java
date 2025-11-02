package net.emilla.action.box;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import net.emilla.action.box.SnippetAdapter.SnippetHolder;
import net.emilla.databinding.SnippetItemBinding;
import net.emilla.struct.sort.SortedArray;

/*internal*/ final class SnippetAdapter extends RecyclerView.Adapter<SnippetHolder> {

    private final SortedArray<String> mLabels;
    private OnItemClickListener mItemClickAction;

    /*internal*/ SnippetAdapter(SortedArray<String> labels, OnItemClickListener itemClickAction) {
        mLabels = labels;
        mItemClickAction = itemClickAction;
    }

    public void setItemClickAction(OnItemClickListener itemClickAction) {
        mItemClickAction = itemClickAction;
    }

    @Override
    public SnippetHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context ctx = parent.getContext();
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
            this.labelView = binding.snippetLabel;
        }
    }

    @FunctionalInterface
    public interface OnItemClickListener {

        void onClick(int pos);
    }
}
