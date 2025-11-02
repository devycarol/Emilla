package net.emilla.command.core;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.emilla.activity.AssistActivity;
import net.emilla.command.CommandYielder;
import net.emilla.command.EmillaCommand;
import net.emilla.config.Aliases;
import net.emilla.config.SettingVals;
import net.emilla.exception.EmillaException;

import java.util.Set;

public abstract class CoreCommand extends EmillaCommand {

    @StringRes
    private final int mName;

    protected CoreCommand(AssistActivity act, CoreEntry coreEntry, int imeAction) {
        super(act, coreEntry, imeAction);

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
