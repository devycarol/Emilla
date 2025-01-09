package net.emilla.command.core;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.content.res.AppCompatResources;

import net.emilla.AssistActivity;
import net.emilla.command.EmillaCommand;
import net.emilla.lang.Lang;
import net.emilla.run.MessageFailure;

public abstract class CoreCommand extends EmillaCommand {

    private final CoreParams mParams;

    protected CoreCommand(AssistActivity act, String instruct, CoreParams params) {
        super(act, instruct, params);
        mParams = params;
    }

    @Override @Deprecated
    protected String dupeLabel() {
        return string(mParams.mName) + " (Emilla command)";
    }

    protected void fail(@StringRes int msg) {
        fail(new MessageFailure(activity, mParams.mName, msg));
    }

    public static abstract class CoreParams implements Params {

        @StringRes
        private final int mName, mInstruction;
        private final boolean mShouldLowercase;
        @DrawableRes
        private final int mIcon;
        private final int mImeAction;

        protected CoreParams(@StringRes int name, @StringRes int instruction, @DrawableRes int icon,
                int imeAction) {
            this(name, instruction, true, icon, imeAction);
        }

        protected CoreParams(@StringRes int name, @StringRes int instruction,
                boolean shouldLowercase, @DrawableRes int icon, int imeAction) {
            mName = name;
            mInstruction = instruction;
            mShouldLowercase = shouldLowercase;
            mIcon = icon;
            mImeAction = imeAction;
        }

        @Override
        public final CharSequence name(Resources res) {
            return res.getString(mName);
        }

        @Override
        public final boolean shouldLowercase() {
            return mShouldLowercase;
        }

        @Override
        public final CharSequence title(Resources res) {
            return Lang.colonConcat(res, mName, mInstruction);
        }

        @Override
        public final Drawable icon(Context ctx) {
            return AppCompatResources.getDrawable(ctx, mIcon);
        }

        @Override
        public final boolean usesAppIcon() {
            return false;
        }

        @Override
        public final int imeAction() {
            return mImeAction;
        }
    }
}
