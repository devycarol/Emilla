package net.emilla.command.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import androidx.annotation.ArrayRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.content.res.AppCompatResources;

import net.emilla.R;
import net.emilla.command.Params;
import net.emilla.config.Aliases;
import net.emilla.config.SettingVals;
import net.emilla.lang.Lang;

import java.util.Arrays;
import java.util.Set;

public enum CoreEntry implements Params {
    WEB(Web.ENTRY, Web::new, R.string.command_web, R.array.aliases_web, R.string.instruction_web, R.drawable.ic_web, R.string.summary_web, R.string.manual_web) {

        @Override
        public boolean isPossible(PackageManager pm) {
            return Web.possible(pm);
        }

    },
    LAUNCH(Launch.ENTRY, Launch::new, R.string.command_launch, R.array.aliases_launch, R.string.instruction_app, R.drawable.ic_launch, R.string.summary_launch, R.string.manual_launch) {

        @Override
        public boolean isPossible(PackageManager pm) {
            return Launch.possible();
        }

    },
    CALL(Call.ENTRY, Call::new, R.string.command_call, R.array.aliases_call, R.string.instruction_phone, R.drawable.ic_call, R.string.summary_call, R.string.manual_call) {

        @Override
        public boolean isPossible(PackageManager pm) {
            return Call.possible(pm);
        }

    },
    DIAL(Dial.ENTRY, Dial::new, R.string.command_dial, R.array.aliases_dial, R.string.instruction_dial, R.drawable.ic_dial, R.string.summary_dial, R.string.manual_dial) {

        @Override
        public boolean isPossible(PackageManager pm) {
            return Dial.possible(pm);
        }

    },
    SMS(Sms.ENTRY, Sms::new, R.string.command_sms, R.array.aliases_sms, R.string.instruction_phone, R.drawable.ic_sms, R.string.summary_sms, R.string.manual_sms) {

        @Override
        public boolean isPossible(PackageManager pm) {
            return Sms.possible(pm);
        }

    },
    CONTACT(Contact.ENTRY, Contact::new, R.string.command_contact, R.array.aliases_contact, R.string.instruction_contact, R.drawable.ic_contact, R.string.summary_contact, R.string.manual_contact) {

        @Override
        public boolean isPossible(PackageManager pm) {
            return Contact.possible(pm);
        }

    },
    PLAY(Play.ENTRY, Play::new, R.string.command_play, R.array.aliases_play, R.string.instruction_play, R.drawable.ic_play, R.string.summary_play, R.string.manual_play) {

        @Override
        public boolean isPossible(PackageManager pm) {
            return Play.possible();
        }

    },
    PAUSE(Pause.ENTRY, Pause::new, R.string.command_pause, R.array.aliases_pause, R.string.instruction_pause, R.drawable.ic_pause, R.string.summary_pause, R.string.manual_pause, false, true) {

        @Override
        public boolean isPossible(PackageManager pm) {
            return Pause.possible();
        }

    },
    NAVIGATE(Navigate.ENTRY, Navigate::new, R.string.command_navigate, R.array.aliases_navigate, R.string.instruction_location, R.drawable.ic_navigate, R.string.summary_navigate, R.string.manual_navigate) {

        @Override
        public boolean isPossible(PackageManager pm) {
            return Navigate.possible(pm);
        }

    },
    WEATHER(Weather.ENTRY, Weather::new, R.string.command_weather, R.array.aliases_weather, R.string.instruction_app, R.drawable.ic_weather, R.string.summary_weather, R.string.manual_weather) {

        @Override
        public boolean isPossible(PackageManager pm) {
            return Weather.possible(pm);
        }

    },
    NOTE(Note.ENTRY, Note::new, R.string.command_note, R.array.aliases_note, R.string.instruction_file, R.drawable.ic_note, R.string.summary_note, R.string.manual_note, true, false) {

        @Override
        public boolean isPossible(PackageManager pm) {
            return false/*Note.possible()*/;
        }

    },
    TODO(Todo.ENTRY, Todo::new, R.string.command_todo, R.array.aliases_todo, R.string.instruction_todo, R.drawable.ic_todo, R.string.summary_todo, R.string.manual_todo, true, false) {

        @Override
        public boolean isPossible(PackageManager pm) {
            return false/*Todo.possible()*/;
        }

    },
    FIND(Find.ENTRY, Find::new, R.string.command_find, R.array.aliases_find, R.string.instruction_find, R.drawable.ic_find, R.string.summary_find, R.string.manual_find, true, false) {

        @Override
        public boolean isPossible(PackageManager pm) {
            return false/*Find.possible()*/;
        }

    },
    EMAIL(Email.ENTRY, Email::new, R.string.command_email, R.array.aliases_email, R.string.instruction_email, R.drawable.ic_email, R.string.summary_email, R.string.manual_email) {

        @Override
        public boolean isPossible(PackageManager pm) {
            return Email.possible(pm);
        }

    },
    SHARE(Share.ENTRY, Share::new, R.string.command_share, R.array.aliases_share, R.string.instruction_app, R.drawable.ic_share, R.string.summary_share, R.string.manual_share) {

        @Override
        public boolean isPossible(PackageManager pm) {
            return Share.possible(pm);
        }

    },
    TORCH(Torch.ENTRY, Torch::new, R.string.command_torch, R.array.aliases_torch, R.string.instruction_torch, R.drawable.ic_torch, R.string.summary_torch, R.string.manual_torch, false, true) {

        @Override
        public boolean isPossible(PackageManager pm) {
            return Torch.possible(pm);
        }

    },
    CALCULATE(Calculate.ENTRY, Calculate::new, R.string.command_calculate, R.array.aliases_calculate, R.string.instruction_calculate, R.drawable.ic_calculate, R.string.summary_calculate, R.string.manual_calculate) {

        @Override
        public boolean isPossible(PackageManager pm) {
            return Calculate.possible();
        }

    },
    TIME(Time.ENTRY, Time::new, R.string.command_time, R.array.aliases_time, R.string.instruction_location, R.drawable.ic_clock, R.string.summary_time, R.string.manual_time) {

        @Override
        public boolean isPossible(PackageManager pm) {
            return Time.possible();
        }

    },
    ALARM(Alarm.ENTRY, Alarm::new, R.string.command_alarm, R.array.aliases_alarm, R.string.instruction_alarm, R.drawable.ic_alarm, R.string.summary_alarm, R.string.manual_alarm) {

        @Override
        public boolean isPossible(PackageManager pm) {
            return Alarm.possible(pm);
        }

    },
    TIMER(Timer.ENTRY, Timer::new, R.string.command_timer, R.array.aliases_timer, R.string.instruction_timer, R.drawable.ic_timer, R.string.summary_timer, R.string.manual_timer) {

        @Override
        public boolean isPossible(PackageManager pm) {
            return Timer.possible(pm);
        }

    },
    POMODORO(Pomodoro.ENTRY, Pomodoro::new, R.string.command_pomodoro, R.array.aliases_pomodoro, R.string.instruction_pomodoro, R.drawable.ic_pomodoro, R.string.summary_pomodoro, R.string.manual_pomodoro) {

        @Override
        public boolean isPossible(PackageManager pm) {
            return Pomodoro.possible();
        }

    },
    CALENDAR(Calendar.ENTRY, Calendar::new, R.string.command_calendar, R.array.aliases_calendar, R.string.instruction_calendar, R.drawable.ic_calendar, R.string.summary_calendar, R.string.manual_calendar) {

        @Override
        public boolean isPossible(PackageManager pm) {
            return Calendar.possible(pm);
        }

    },
    NOTIFY(Notify.ENTRY, Notify::new, R.string.command_notify, R.array.aliases_notify, R.string.instruction_title, R.drawable.ic_notify, R.string.summary_notify, R.string.manual_notify) {

        @Override
        public boolean isPossible(PackageManager pm) {
            return Notify.possible();
        }

    },
    COPY(Copy.ENTRY, Copy::new, R.string.command_copy, R.array.aliases_copy, R.string.instruction_text, R.drawable.ic_copy, R.string.summary_copy, R.string.manual_copy) {

        @Override
        public boolean isPossible(PackageManager pm) {
            return Copy.possible();
        }

    },
    SNIPPETS(Snippets.ENTRY, Snippets::new, R.string.command_snippets, R.array.aliases_snippets, R.string.instruction_name_label, R.drawable.ic_snippets, R.string.summary_snippets, R.string.manual_snippets) {

        @Override
        public boolean isPossible(PackageManager pm) {
            return Snippets.possible();
        }

    },
    SETTING(Setting.ENTRY, Setting::new, R.string.command_setting, R.array.aliases_setting, R.string.instruction_setting, R.drawable.ic_settings, R.string.summary_setting, R.string.manual_setting, true, false) {

        @Override
        public boolean isPossible(PackageManager pm) {
            return false/*Setting.possible()*/;
        }

    },
    CELSIUS(Celsius.ENTRY, Celsius::new, R.string.command_celsius, R.array.aliases_celsius, R.string.instruction_temperature, R.drawable.ic_temperature, R.string.summary_celsius, R.string.manual_celsius) {

        @Override
        public boolean isPossible(PackageManager pm) {
            return Celsius.possible();
        }

    },
    FAHRENHEIT(Fahrenheit.ENTRY, Fahrenheit::new, R.string.command_fahrenheit, R.array.aliases_fahrenheit, R.string.instruction_temperature, R.drawable.ic_temperature, R.string.summary_fahrenheit, R.string.manual_fahrenheit) {

        @Override
        public boolean isPossible(PackageManager pm) {
            return Fahrenheit.possible();
        }

    },
    ROLL(Roll.ENTRY, Roll::new, R.string.command_roll, R.array.aliases_roll, R.string.instruction_roll, R.drawable.ic_roll, R.string.summary_roll, R.string.manual_roll) {

        @Override
        public boolean isPossible(PackageManager pm) {
            return Roll.possible();
        }

    },
    RANDOM_NUMBER(RandomNumber.ENTRY, RandomNumber::new, R.string.command_random_number, R.array.aliases_random_number, R.string.instruction_text, R.drawable.ic_random_number, R.string.summary_random_number, R.string.manual_random_number) {

        @Override
        public boolean isPossible(PackageManager pm) {
            return RandomNumber.possible();
        }

    },
    INFO(Info.ENTRY, Info::new, R.string.command_info, R.array.aliases_info, R.string.instruction_app, R.drawable.ic_info, R.string.summary_info, R.string.manual_info) {

        @Override
        public boolean isPossible(PackageManager pm) {
            return Info.possible(pm);
        }

    },
    NOTIFICATIONS(Notifications.ENTRY, Notifications::new, R.string.command_notifications, R.array.aliases_notifications, R.string.instruction_app, R.drawable.ic_notifications, R.string.summary_notifications, R.string.manual_notifications) {

        @Override
        public boolean isPossible(PackageManager pm) {
            return Notifications.possible(pm);
        }

    },
    UNINSTALL(Uninstall.ENTRY, Uninstall::new, R.string.command_uninstall, R.array.aliases_uninstall, R.string.instruction_app, R.drawable.ic_uninstall, R.string.summary_uninstall, R.string.manual_uninstall) {

        @Override
        public boolean isPossible(PackageManager pm) {
            return Uninstall.possible(pm);
        }

    },
    TOAST(Toast.ENTRY, Toast::new, R.string.command_toast, R.array.aliases_toast, R.string.instruction_text, R.drawable.ic_toast, R.string.summary_toast, R.string.manual_toast) {

        @Override
        public boolean isPossible(PackageManager pm) {
            return Toast.possible();
        }

    },
    BITS(Bits.ENTRY, Bits::new, R.string.command_bits, R.array.aliases_bits, R.string.instruction_calculate, R.drawable.ic_command, R.string.summary_bits, R.string.manual_bits) {

        @Override
        public boolean isPossible(PackageManager pm) {
            return Bits.possible();
        }

    };

    public final String entry;
    /*internal*/ final CoreMaker mMaker;
    @StringRes
    public final int name;
    @ArrayRes
    public final int aliases;
    @StringRes
    public final int instruction;
    @DrawableRes
    public final int icon;
    @StringRes
    public final int summary;
    @StringRes
    public final int manual;
    public final boolean usesInstruction;
    public final boolean isImplemented;

    CoreEntry(
        String entry,
        CoreMaker maker,
        @StringRes int name,
        @ArrayRes int aliases,
        @StringRes int instruction,
        @DrawableRes int icon,
        @StringRes int summary,
        @StringRes int manual
    ) {
        this(entry, maker, name, aliases, instruction, icon, summary, manual, true, true);
    }

    CoreEntry(
        String entry,
        CoreMaker maker,
        @StringRes int name,
        @ArrayRes int aliases,
        @StringRes int instruction,
        @DrawableRes int icon,
        @StringRes int summary,
        @StringRes int manual,
        boolean usesInstruction,
        boolean isImplemented
    ) {
        this.entry = entry;
        mMaker = maker;
        this.name = name;
        this.aliases = aliases;
        this.instruction = instruction;
        this.icon = icon;
        this.summary = summary;
        this.manual = manual;
        this.usesInstruction = usesInstruction;
        this.isImplemented = isImplemented;
    }

    public abstract boolean isPossible(PackageManager pm);
    // todo: be more granular about deactivating certain command elements based on which intents
    //  are/n't doable. currently these methods are generally permissive if just one of their
    //  intents is doable.

    public final boolean enabled(PackageManager pm, SharedPreferences prefs) {
        return isImplemented && SettingVals.commandEnabled(pm, prefs, this);
    }

    public final CoreYielder yielder() {
        return new CoreYielder(this);
    }

    @Override
    public final String name(Resources res) {
        return res.getString(name);
    }

    @Override
    public final CharSequence title(Resources res) {
        return Lang.colonConcat(res, name, instruction);
    }

    @Nullable
    public Set<String> aliases(SharedPreferences prefs, Resources res) {
        return Aliases.coreSet(prefs, res, entry, aliases);
    }

    @Override
    public final Drawable icon(Context ctx) {
        return AppCompatResources.getDrawable(ctx, icon);
    }

    public static String[] entryNames(Resources res) {
        return Arrays.stream(values())
            .filter(coreEntry -> coreEntry.isImplemented)
            .map(coreEntry -> coreEntry.name(res))
            .toArray(String[]::new);
    }

    public static String[] entryValues() {
        return Arrays.stream(values())
            .filter(coreEntry -> coreEntry.isImplemented)
            .map(coreEntry -> coreEntry.entry)
            .toArray(String[]::new);
    }

    public static CoreEntry of(String entry) {
        return switch (entry) {
            case Call.ENTRY -> CALL;
            case Dial.ENTRY -> DIAL;
            case Sms.ENTRY -> SMS;
            case Email.ENTRY -> EMAIL;
            case Navigate.ENTRY -> NAVIGATE;
            case Copy.ENTRY -> COPY;
            case Snippets.ENTRY -> SNIPPETS;
            case Share.ENTRY -> SHARE;
            case Launch.ENTRY -> LAUNCH;
            case Setting.ENTRY -> SETTING;
            case Note.ENTRY -> NOTE;
            case Todo.ENTRY -> TODO;
            case Web.ENTRY -> WEB;
            case Find.ENTRY -> FIND;
            case Time.ENTRY -> TIME;
            case Alarm.ENTRY -> ALARM;
            case Timer.ENTRY -> TIMER;
            case Pomodoro.ENTRY -> POMODORO;
            case Calendar.ENTRY -> CALENDAR;
            case Contact.ENTRY -> CONTACT;
            case Notify.ENTRY -> NOTIFY;
            case Calculate.ENTRY -> CALCULATE;
            case RandomNumber.ENTRY -> RANDOM_NUMBER;
            case Celsius.ENTRY -> CELSIUS;
            case Fahrenheit.ENTRY -> FAHRENHEIT;
            case Roll.ENTRY -> ROLL;
            case Bits.ENTRY -> BITS;
            case Weather.ENTRY -> WEATHER;
            case Play.ENTRY -> PLAY;
            case Pause.ENTRY -> PAUSE;
            case Torch.ENTRY -> TORCH;
            case Info.ENTRY -> INFO;
            case Notifications.ENTRY -> NOTIFICATIONS;
            case Uninstall.ENTRY -> UNINSTALL;
            case Toast.ENTRY -> TOAST;
            default -> throw new IllegalArgumentException("Invalid command entry");
        };
    }

}
