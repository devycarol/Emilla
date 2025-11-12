package net.emilla.file;

import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import net.emilla.databinding.ListItemBinding;

public final class ListItemHolder extends RecyclerView.ViewHolder {

    public final TextView labelView;

    public ListItemHolder(ListItemBinding binding) {
        super(binding.getRoot());

        this.labelView = binding.getRoot();
    }

}
