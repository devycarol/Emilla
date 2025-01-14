package net.emilla.command.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import androidx.annotation.ArrayRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.content.res.AppCompatResources;

import net.emilla.AssistActivity;
import net.emilla.command.CommandYielder;
import net.emilla.command.EmillaCommand;
import net.emilla.lang.Lang;
import net.emilla.run.MessageFailure;
import net.emilla.settings.Aliases;

import java.util.Set;

public abstract class CoreCommand extends EmillaCommand {

    private final CoreParams mParams;

    protected CoreCommand(AssistActivity act, CoreParams params) {
        super(act, params);
        mParams = params;
    }

    @Override @Deprecated
    protected String dupeLabel() {
        return string(mParams.mName) + " (Emilla command)";
    }

    protected void fail(@StringRes int msg) {
        fail(new MessageFailure(activity, mParams.mName, msg));
    }

    public static class Yielder extends CommandYielder {

        private final boolean mUsesInstruction;
        private final Maker mMaker;
        @StringRes
        private final int mName;
        private final String mPrefsEntry;
        @ArrayRes
        private final int mAliases;

        public Yielder(boolean usesInstruction, Maker maker, String prefsEntry, @StringRes int name,
                @ArrayRes int aliases) {
            mUsesInstruction = usesInstruction;
            mMaker = maker;
            mName = name;
            mPrefsEntry = prefsEntry;
            mAliases = aliases;
        }

        @Override
        public final boolean isPrefixable() {
            return mUsesInstruction;
        }

        @Override
        protected final EmillaCommand makeCommand(AssistActivity act) {
            return mMaker.make(act);
        }

        public final String name(Resources res) {
            return res.getString(mName);
        }

        public final Set<String> aliases(SharedPreferences prefs, Resources res) {
            return Aliases.coreSet(prefs, res, mPrefsEntry, mAliases);
        }
    }

    public interface Maker {

        CoreCommand make(AssistActivity act);
    }

    public static abstract class CoreParams implements Params {

        @StringRes
        private final int mName, mInstruction;
        private final boolean mShouldLowercase;
        @DrawableRes
        private final int mIcon;
        private final int mImeAction;
        @StringRes
        private final int mSummary, mManual;

        protected CoreParams(@StringRes int name, @StringRes int instruction, @DrawableRes int icon,
                int imeAction, @StringRes int summary, @StringRes int manual) {
            this(name, instruction, true, icon, imeAction, summary, manual);
        }

        protected CoreParams(@StringRes int name, @StringRes int instruction,
                boolean shouldLowercase, @DrawableRes int icon, int imeAction,
                @StringRes int summary, @StringRes int manual) {
            mName = name;
            mInstruction = instruction;
            mShouldLowercase = shouldLowercase;
            mIcon = icon;
            mImeAction = imeAction;
            mSummary = summary;
            mManual = manual;
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

        @Override
        public int summary() {
            return mSummary;
        }

        @Override
        public int manual() {
            return mManual;
        }
    }
}
