package net.emilla.file;

import android.content.res.Resources;
import android.os.Build;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;

import net.emilla.R;
import net.emilla.databinding.ChecklistItemBinding;
import net.emilla.util.Views;

public final class ChecklistItemHolder extends RecyclerView.ViewHolder {

    public final TextView label;
    private final CheckBox mCheckBox;

    public ChecklistItemHolder(ChecklistItemBinding binding) {
        super(binding.getRoot());

        this.label = binding.label;
        mCheckBox = binding.checkBox;
    }

    public void setChecked(Resources res, boolean isChecked) {
        if (isChecked) {
            mCheckBox.setVisibility(View.VISIBLE);
            setStateDescription(res, R.string.checked);
            Views.setClickActionLabel(res, this.itemView, R.string.uncheck);
        } else {
            mCheckBox.setVisibility(View.INVISIBLE);
            removeStateDescription();
            Views.setClickActionLabel(res, this.itemView, R.string.check);
        }
    }

    private void setStateDescription(Resources res, @StringRes int description) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            this.itemView.setStateDescription(res.getString(description));
        } else {
            CharSequence content = this.label.getText();
            Views.setStateDescriptionCompat(res, this.itemView, content, description);
        }
    }

    private void removeStateDescription() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            this.itemView.setStateDescription(null);
        } else {
            CharSequence content = this.label.getText();
            Views.removeStateDescriptionCompat(this.itemView, content);
        }
    }

}
