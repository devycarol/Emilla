package net.emilla.action;

import android.content.res.Resources;

import androidx.annotation.StringRes;

public interface LabeledQuickAction extends QuickAction {

    @StringRes
    int label();
    @StringRes
    int description();

    @Override
    default String label(Resources res) {
        return res.getString(label());
    }

    @Override
    default String description(Resources res) {
        return res.getString(description());
    }
}
