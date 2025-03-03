package net.emilla.ping;

import static android.app.NotificationManager.IMPORTANCE_DEFAULT;

import android.app.NotificationChannel;
import android.content.res.Resources;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;

import net.emilla.R;

public final class PingChannel {

    public static final int SLOT_UNLIMITED = 0;
    private static final int SLOT_POMODORO = 1;

    public static final String
            COMMAND = "command",
            POMODORO_START = "pomodoro_start",
            POMODORO_WARN = "pomodoro_warn",
            POMODORO_END = "pomodoro_end",
            POMODORO_BREAK_START = "pomodoro_break_start",
            POMODORO_BREAK_WARN = "pomodoro_break_warn",
            POMODORO_BREAK_END = "pomodoro_break_end";

    public static PingChannel of(String id) {
        return switch (id) {
            case COMMAND -> command();
            case POMODORO_START -> pomodoroStart();
            case POMODORO_WARN -> pomodoroWarning();
            case POMODORO_END -> pomodoroEnd();
            case POMODORO_BREAK_START -> pomodoroBreakStart();
            case POMODORO_BREAK_WARN -> pomodoroBreakWarning();
            case POMODORO_BREAK_END -> pomodoroBreakEnd();
            default -> throw new IllegalArgumentException();
        };
    }

    public static PingChannel command() {
        return new PingChannel(COMMAND, SLOT_UNLIMITED,
                               R.string.ping_channel_command,
                               R.string.channel_desc_command);
    }

    private static PingChannel pomodoroStart() {
        return new PingChannel(POMODORO_START, SLOT_POMODORO,
                               R.string.ping_channel_pomodoro_start,
                               R.string.channel_desc_pomodoro_start);
    }

    private static PingChannel pomodoroWarning() {
        return new PingChannel(POMODORO_WARN, SLOT_POMODORO,
                               R.string.ping_channel_pomodoro_warn,
                               R.string.channel_desc_pomodoro_warn);
    }

    private static PingChannel pomodoroEnd() {
        return new PingChannel(POMODORO_END, SLOT_POMODORO,
                               R.string.ping_channel_pomodoro_end,
                               R.string.channel_desc_pomodoro_end);
    }

    private static PingChannel pomodoroBreakStart() {
        return new PingChannel(POMODORO_BREAK_START, SLOT_POMODORO,
                               R.string.ping_channel_pomodoro_break_start,
                               R.string.channel_desc_pomodoro_break_start);
    }

    private static PingChannel pomodoroBreakWarning() {
        return new PingChannel(POMODORO_BREAK_WARN, SLOT_POMODORO,
                               R.string.ping_channel_pomodoro_break_warn,
                               R.string.channel_desc_pomodoro_break_warn);
    }

    private static PingChannel pomodoroBreakEnd() {
        return new PingChannel(POMODORO_BREAK_END, SLOT_POMODORO,
                               R.string.ping_channel_pomodoro_break_end,
                               R.string.channel_desc_pomodoro_break_end);
    }

    public final String id;
    public final int slot;
    @StringRes
    private final int mName, mDescription;

    private PingChannel(String id, int slot, @StringRes int name, @StringRes int desc) {
        this.id = id;
        this.slot = slot;
        mName = name;
        mDescription = desc;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public NotificationChannel make(Resources res) {
        var channel = new NotificationChannel(id, res.getString(mName), IMPORTANCE_DEFAULT);
        channel.setDescription(res.getString(mDescription));
        return channel;
    }
}
