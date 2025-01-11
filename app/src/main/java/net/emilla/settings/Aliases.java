package net.emilla.settings;

import android.content.SharedPreferences;
import android.content.res.Resources;

import androidx.annotation.ArrayRes;
import androidx.annotation.Nullable;

import net.emilla.R;
import net.emilla.command.app.AospContacts;
import net.emilla.command.app.AppCommand.AppInfo;
import net.emilla.command.app.Discord;
import net.emilla.command.app.Firefox;
import net.emilla.command.app.Github;
import net.emilla.command.app.Markor;
import net.emilla.command.app.Newpipe;
import net.emilla.command.app.Outlook;
import net.emilla.command.app.Signal;
import net.emilla.command.app.Tor;
import net.emilla.command.app.Tubular;
import net.emilla.command.app.Youtube;
import net.emilla.command.core.Alarm;
import net.emilla.command.core.Bookmark;
import net.emilla.command.core.Calculate;
import net.emilla.command.core.Calendar;
import net.emilla.command.core.Call;
import net.emilla.command.core.Contact;
import net.emilla.command.core.Copy;
import net.emilla.command.core.Dial;
import net.emilla.command.core.Email;
import net.emilla.command.core.Info;
import net.emilla.command.core.Launch;
import net.emilla.command.core.Navigate;
import net.emilla.command.core.Pomodoro;
import net.emilla.command.core.Settings;
import net.emilla.command.core.Share;
import net.emilla.command.core.Sms;
import net.emilla.command.core.Time;
import net.emilla.command.core.Timer;
import net.emilla.command.core.Toast;
import net.emilla.command.core.Torch;
import net.emilla.command.core.Uninstall;
import net.emilla.command.core.Weather;
import net.emilla.command.core.Web;

import java.util.Set;

public class Aliases {

    @ArrayRes
    private static final int[] SETS = {
            Call.ALIASES,
            Dial.ALIASES,
            Sms.ALIASES,
            Email.ALIASES,
            Navigate.ALIASES,
            Launch.ALIASES,
            Copy.ALIASES,
            Share.ALIASES,
            Settings.ALIASES,
//            Note.ALIASES,
//            Todo.ALIASES,
            Web.ALIASES,
//            Find.ALIASES,
            Time.ALIASES,
            Alarm.ALIASES,
            Timer.ALIASES,
            Pomodoro.ALIASES,
            Calendar.ALIASES,
            Contact.ALIASES,
//            Notify.ALIASES,
            Calculate.ALIASES,
            Weather.ALIASES,
            Bookmark.ALIASES,
            Torch.ALIASES,
            Info.ALIASES,
            Uninstall.ALIASES,
            Toast.ALIASES,
    };

    private static final String[] PREFS = {
            "aliases_" + Call.ENTRY,
            "aliases_" + Dial.ENTRY,
            "aliases_" + Sms.ENTRY,
            "aliases_" + Email.ENTRY,
            "aliases_" + Navigate.ENTRY,
            "aliases_" + Launch.ENTRY,
            "aliases_" + Copy.ENTRY,
            "aliases_" + Share.ENTRY,
            "aliases_" + Settings.ENTRY,
//            "aliases_" + Note.ENTRY,
//            "aliases_" + Todo.ENTRY,
            "aliases_" + Web.ENTRY,
//            "aliases_" + Find.ENTRY,
            "aliases_" + Time.ENTRY,
            "aliases_" + Alarm.ENTRY,
            "aliases_" + Timer.ENTRY,
            "aliases_" + Pomodoro.ENTRY,
            "aliases_" + Calendar.ENTRY,
            "aliases_" + Contact.ENTRY,
//            "aliases_" + Notify.ENTRY,
            "aliases_" + Calculate.ENTRY,
            "aliases_" + Weather.ENTRY,
            "aliases_" + Bookmark.ENTRY,
            "aliases_" + Torch.ENTRY,
            "aliases_" + Info.ENTRY,
            "aliases_" + Uninstall.ENTRY,
            "aliases_" + Toast.ENTRY,
    };

    public static String textKey(String entry) {
        return "aliases_" + entry + "_text";
    }

    public static Set<String> set(SharedPreferences prefs, Resources res, int idx) {
        return prefs.getStringSet(PREFS[idx], Set.of(res.getStringArray(SETS[idx])));
    }

    @ArrayRes
    public static int appSetId(String pkg, String cls) {
        return switch (pkg) {
            case AospContacts.PKG -> R.array.aliases_aosp_contacts;
            case Markor.PKG -> cls.equals(Markor.CLS_MAIN) ? R.array.aliases_markor : 0;
            // Markor can have multiple launchers, only the main one should have the aliases.
            case Firefox.PKG -> R.array.aliases_firefox;
            case Tor.PKG -> R.array.aliases_tor;
            case Signal.PKG -> R.array.aliases_signal;
            case Newpipe.PKG -> R.array.aliases_newpipe;
            case Tubular.PKG -> R.array.aliases_tubular;
            case Github.PKG -> R.array.aliases_github;
            case Youtube.PKG -> R.array.aliases_youtube;
            case Discord.PKG -> R.array.aliases_discord;
            case Outlook.PKG -> R.array.aliases_outlook;
            default -> 0;
        };
    }

    @Nullable
    public static Set<String> appSet(SharedPreferences prefs, Resources res, AppInfo info) {
        @ArrayRes int setId = appSetId(info.pkg, info.cls);
        if (setId != 0) {
            return prefs.getStringSet("aliases_" + info.pkg, Set.of(res.getStringArray(setId)));
        }
        return null;
    }
}
