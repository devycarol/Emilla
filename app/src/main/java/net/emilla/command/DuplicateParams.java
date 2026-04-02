package net.emilla.command;

import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.annotation.internal;
import net.emilla.lang.Lang;
import net.emilla.widget.SymbolIcon;

final class DuplicateParams implements Params {
    @StringRes
    private static final int NAME = R.string.command_duplicate;

    @internal DuplicateParams() {
    }

    @Override
    public String name(Resources res) {
        return res.getString(NAME);
    }

    @Override
    public CharSequence title(Resources res) {
        return Lang.colonConcat(res, NAME, R.string.instruction_duplicate);
    }

    @Override
    public SymbolIcon actionIcon(Context ctx) {
        return new SymbolIcon(R.drawable.ic_command);
    }

    @Override
    public boolean isProperNoun() {
        return false;
    }
}
