package net.emilla.ping;

import static android.app.NotificationManager.IMPORTANCE_DEFAULT;

import android.app.NotificationChannel;
import android.content.res.Resources;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;

import net.emilla.R;

public enum PingChannel {
    COMMAND(
        PingChannels.ID_COMMAND,
        PingChannels.SLOT_UNLIMITED,
        R.string.ping_channel_command,
        R.string.channel_desc_command
    ),
    POMODORO_START(
        PingChannels.ID_POMODORO_START,
        PingChannels.SLOT_POMODORO,
        R.string.ping_channel_pomodoro_start,
        R.string.channel_desc_pomodoro_start
    ),
    POMODORO_WARNING(
        PingChannels.ID_POMODORO_WARNING,
        PingChannels.SLOT_POMODORO,
        R.string.ping_channel_pomodoro_warn,
        R.string.channel_desc_pomodoro_warn
    ),
    POMODORO_END(
        PingChannels.ID_POMODORO_END,
        PingChannels.SLOT_POMODORO,
        R.string.ping_channel_pomodoro_end,
        R.string.channel_desc_pomodoro_end
    ),
    POMODORO_BREAK_START(
        PingChannels.ID_POMODORO_BREAK_START,
        PingChannels.SLOT_POMODORO,
        R.string.ping_channel_pomodoro_break_start,
        R.string.channel_desc_pomodoro_break_start
    ),
    POMODORO_BREAK_WARNING(
        PingChannels.ID_POMODORO_BREAK_WARNING,
        PingChannels.SLOT_POMODORO,
        R.string.ping_channel_pomodoro_break_warn,
        R.string.channel_desc_pomodoro_break_warn
    ),
    POMODORO_BREAK_END(
        PingChannels.ID_POMODORO_BREAK_END,
        PingChannels.SLOT_POMODORO,
        R.string.ping_channel_pomodoro_break_end,
        R.string.channel_desc_pomodoro_break_end
    );

    public static PingChannel of(String id) {
        return switch (id) {
            case PingChannels.ID_COMMAND -> COMMAND;
            case PingChannels.ID_POMODORO_START -> POMODORO_START;
            case PingChannels.ID_POMODORO_WARNING -> POMODORO_WARNING;
            case PingChannels.ID_POMODORO_END -> POMODORO_END;
            case PingChannels.ID_POMODORO_BREAK_START -> POMODORO_BREAK_START;
            case PingChannels.ID_POMODORO_BREAK_WARNING -> POMODORO_BREAK_WARNING;
            case PingChannels.ID_POMODORO_BREAK_END -> POMODORO_BREAK_END;
            default -> throw new IllegalArgumentException("Invalid ping channel ID");
        };
    }

    public final String id;
    public final int slot;
    @StringRes
    private final int mName;
    @StringRes
    private final int mDescription;

    PingChannel(String id, int slot, @StringRes int name, @StringRes int desc) {
        this.id = id;
        this.slot = slot;
        mName = name;
        mDescription = desc;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public final NotificationChannel make(Resources res) {
        var channel = new NotificationChannel(id, res.getString(mName), IMPORTANCE_DEFAULT);
        channel.setDescription(res.getString(mDescription));
        return channel;
    }

}
