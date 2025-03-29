package net.emilla.command.core;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import androidx.annotation.ArrayRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.content.res.AppCompatResources;

import net.emilla.activity.AssistActivity;
import net.emilla.command.CommandYielder;
import net.emilla.command.EmillaCommand;
import net.emilla.exception.EmillaException;
import net.emilla.lang.Lang;
import net.emilla.settings.Aliases;
import net.emilla.settings.SettingVals;

import java.util.Set;

public abstract class CoreCommand extends EmillaCommand {

    public static boolean possible(PackageManager pm, String entry) {
        // todo: be more granular about deactivating certain command elements based on which intents
        //  are/n't doable. currently these methods are generally permissive if just one of their
        //  intents is doable.
        return switch (entry) {
            case Call.ENTRY -> Call.possible(pm);
            case Dial.ENTRY -> Dial.possible(pm);
            case Sms.ENTRY -> Sms.possible(pm);
            case Email.ENTRY -> Email.possible(pm);
            case Navigate.ENTRY -> Navigate.possible(pm);
            case Launch.ENTRY -> Launch.possible();
            case Copy.ENTRY -> Copy.possible();
            case Snippets.ENTRY -> Snippets.possible();
            case Share.ENTRY -> Share.possible(pm);
//            case Setting.ENTRY -> Setting.possible(pm);
//            case Note.ENTRY -> Note.possible(pm);
//            case Todo.ENTRY -> Todo.possible(pm);
            case Web.ENTRY -> Web.possible(pm);
//            case Find.ENTRY -> Find.possible(pm);
            case Time.ENTRY -> Time.possible();
            case Alarm.ENTRY -> Alarm.possible(pm);
            case Timer.ENTRY -> Timer.possible(pm);
            case Pomodoro.ENTRY -> Pomodoro.possible();
            case Calendar.ENTRY -> Calendar.possible(pm);
            case Contact.ENTRY -> Contact.possible(pm);
            case Notify.ENTRY -> Notify.possible();
            case Calculate.ENTRY -> Calculate.possible();
            case RandomNumber.ENTRY -> RandomNumber.possible();
            case Roll.ENTRY -> Roll.possible();
            case Bits.ENTRY -> Bits.possible();
            case Weather.ENTRY -> Weather.possible(pm);
            case Play.ENTRY -> Play.possible();
            case Pause.ENTRY -> Pause.possible();
            case Torch.ENTRY -> Torch.possible(pm);
            case Info.ENTRY -> Info.possible(pm);
            case Uninstall.ENTRY -> Uninstall.possible(pm);
            case Toast.ENTRY -> Toast.possible();
            default -> throw new IllegalArgumentException("No such command \"" + entry + "\".");
        };
    }

    protected static boolean canDo(PackageManager pm, Intent intent) {
        return pm.resolveActivity(intent, 0) != null;
    }

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

        private final String mEntry;
        private final boolean mUsesInstruction;
        private final Maker mMaker;
        @StringRes
        private final int mName;
        @ArrayRes
        private final int mAliases;

        public Yielder(
            boolean usesInstruction,
            Maker maker,
            String entry,
            @StringRes int name,
            @ArrayRes int aliases
        ) {
            mEntry = entry;
            mUsesInstruction = usesInstruction;
            mMaker = maker;
            mName = name;
            mAliases = aliases;
        }

        public boolean enabled(PackageManager pm, SharedPreferences prefs) {
            return SettingVals.commandEnabled(pm, prefs, mEntry);
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

        @Nullable
        public Set<String> aliases(SharedPreferences prefs, Resources res) {
            return Aliases.coreSet(prefs, res, mEntry, mAliases);
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
