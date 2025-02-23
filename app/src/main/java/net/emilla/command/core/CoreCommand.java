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
import net.emilla.exception.EmlaBadCommandException;
import net.emilla.lang.Lang;
import net.emilla.settings.Aliases;

import java.util.Set;

public abstract class CoreCommand extends EmillaCommand {

    public record CoreParams(
        @StringRes int name,
        @StringRes int instruction,
        @DrawableRes int icon
    ) implements Params {

        @Override
        public CharSequence name(Resources res) {
            return res.getString(name);
        }

        @Override
        public CharSequence title(Resources res) {
            return Lang.colonConcat(res, name, instruction);
        }

        @Override
        public Drawable icon(Context ctx) {
            return AppCompatResources.getDrawable(ctx, icon);
        }
    }

    @StringRes
    private final int mName;

    protected CoreCommand(
        AssistActivity act,
        @StringRes int name,
        @StringRes int instruction,
        @DrawableRes int icon,
        @StringRes int summary,
        @StringRes int manual,
        int imeAction
    ) {
        super(act, new CoreParams(name, instruction, icon),
              summary,
              manual,
              imeAction);

        mName = name;
    }

    @Override
    protected boolean shouldLowercase() {
        return true;
    }

    @Override
    public final boolean usesAppIcon() {
        return false;
    }

    @Override @Deprecated
    protected String dupeLabel() {
        return str(mName) + " (Emilla command)";
    }

    public static final class Yielder extends CommandYielder {

        private final boolean mUsesInstruction;
        private final Maker mMaker;
        @StringRes
        private final int mName;
        private final String mPrefsEntry;
        @ArrayRes
        private final int mAliases;

        public Yielder(
            boolean usesInstruction,
            Maker maker,
            String prefsEntry,
            @StringRes int name,
            @ArrayRes int aliases
        ) {
            mUsesInstruction = usesInstruction;
            mMaker = maker;
            mName = name;
            mPrefsEntry = prefsEntry;
            mAliases = aliases;
        }

        @Override
        public boolean isPrefixable() {
            return mUsesInstruction;
        }

        @Override
        protected EmillaCommand makeCommand(AssistActivity act) {
            return mMaker.make(act);
        }

        public String name(Resources res) {
            return res.getString(mName);
        }

        public Set<String> aliases(SharedPreferences prefs, Resources res) {
            return Aliases.coreSet(prefs, res, mPrefsEntry, mAliases);
        }
    }

    public interface Maker {

        CoreCommand make(AssistActivity act);
    }

    protected final EmlaBadCommandException badCommand(@StringRes int msg) {
        return new EmlaBadCommandException(mName, msg);
    }
}
