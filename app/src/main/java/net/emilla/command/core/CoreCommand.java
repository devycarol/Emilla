package net.emilla.command.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.content.res.AppCompatResources;

import net.emilla.activity.AssistActivity;
import net.emilla.command.CommandYielder;
import net.emilla.command.EmillaCommand;
import net.emilla.config.Aliases;
import net.emilla.config.SettingVals;
import net.emilla.exception.EmillaException;
import net.emilla.lang.Lang;

import java.util.Set;

public abstract class CoreCommand extends EmillaCommand {

    public static final class CoreParams implements Params {

        @StringRes
        private final int name;
        @StringRes
        private final int instruction;
        @DrawableRes
        private final int icon;

        public CoreParams(CoreEntry coreEntry) {
            this(coreEntry.name, coreEntry.instruction, coreEntry.icon);
        }

        public CoreParams(@StringRes int name, @StringRes int instruction, @DrawableRes int icon) {
            this.name = name;
            this.instruction = instruction;
            this.icon = icon;
        }

        @Override
        public String name(Resources res) {
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

    protected CoreCommand(AssistActivity act, CoreEntry coreEntry, int imeAction) {
        super(
            act, new CoreParams(coreEntry),

            coreEntry.summary, coreEntry.manual,

            imeAction
        );

        mName = coreEntry.name;
    }

    @Override
    protected /*open*/ boolean shouldLowercase() {
        return true;
    }

    @Override
    public final boolean usesAppIcon() {
        return false;
    }

    @Override @Deprecated
    protected final String dupeLabel() {
        return str(mName) + " (Emilla command)";
    }

    public static final class Yielder extends CommandYielder {

        private final CoreEntry mCoreEntry;
        private final boolean mUsesInstruction;

        public Yielder(CoreEntry coreEntry, boolean usesInstruction) {
            mCoreEntry = coreEntry;
            mUsesInstruction = usesInstruction;
        }

        public boolean enabled(PackageManager pm, SharedPreferences prefs) {
            return SettingVals.commandEnabled(pm, prefs, mCoreEntry);
        }

        @Override
        public boolean isPrefixable() {
            return mUsesInstruction;
        }

        @Override
        protected EmillaCommand makeCommand(AssistActivity act) {
            return mCoreEntry.maker.make(act);
        }

        public String name(Resources res) {
            return res.getString(mCoreEntry.name);
        }

        @Nullable
        public Set<String> aliases(SharedPreferences prefs, Resources res) {
            return Aliases.coreSet(prefs, res, mCoreEntry.entry, mCoreEntry.aliases);
        }
    }

    @FunctionalInterface
    public interface Maker {

        CoreCommand make(AssistActivity act);
    }

    protected final EmillaException badCommand(@StringRes int msg) {
        return new EmillaException(mName, msg);
    }
}
