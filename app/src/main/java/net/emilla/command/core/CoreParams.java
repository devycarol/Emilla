package net.emilla.command.core;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import androidx.appcompat.content.res.AppCompatResources;

import net.emilla.command.Params;
import net.emilla.lang.Lang;

public final class CoreParams implements Params {

    private final int mName;
    private final int mInstruction;
    private final int mIcon;

    public CoreParams(int name, int instruction, int icon) {
        mName = name;
        mInstruction = instruction;
        mIcon = icon;
    }

    @Override
    public String name(Resources res) {
        return res.getString(mName);
    }

    @Override
    public CharSequence title(Resources res) {
        return Lang.colonConcat(res, mName, mInstruction);
    }

    @Override
    public Drawable icon(Context ctx) {
        return AppCompatResources.getDrawable(ctx, mIcon);
    }

}
