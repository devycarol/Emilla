package net.emilla.command;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import androidx.annotation.StringRes;
import androidx.appcompat.content.res.AppCompatResources;

import net.emilla.R;
import net.emilla.lang.Lang;

/*internal*/ final class DuplicateParams implements Params {

    @StringRes
    private static final int NAME = R.string.command_duplicate;

    /*internal*/ DuplicateParams() {}

    @Override
    public String name(Resources res) {
        return res.getString(NAME);
    }

    @Override
    public CharSequence title(Resources res) {
        return Lang.colonConcat(res, NAME, R.string.instruction_duplicate);
    }

    @Override
    public Drawable icon(Context ctx) {
        return AppCompatResources.getDrawable(ctx, R.drawable.ic_command);
    }

}
