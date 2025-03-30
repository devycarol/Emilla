package net.emilla.command.core;

import android.Manifest;
import android.annotation.SuppressLint;

import androidx.annotation.ArrayRes;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.ping.PingChannel;
import net.emilla.ping.Pings;
import net.emilla.util.Permissions;

public final class Notify extends CoreDataCommand {

    public static final String ENTRY = "notify";
    @StringRes
    public static final int NAME = R.string.command_notify;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_notify;

    public static Yielder yielder() {
        return new Yielder(true, Notify::new, ENTRY, NAME, ALIASES);
    }

    public static boolean possible() {
        return true;
    }

    private Notify(AssistActivity act) {
        super(act, NAME,
              R.string.instruction_title,
              R.drawable.ic_notify,
              R.string.summary_notify,
              R.string.manual_notify,
              R.string.data_hint_notify);
    }

    @Override
    protected void run() {
        tryPing(str(R.string.ping_command), null);
    }

    @Override
    protected void run(String title) {
        tryPing(title, null);
    }

    @Override
    protected void runWithData(String text) {
        tryPing(str(R.string.ping_command), text);
    }

    @Override
    protected void runWithData(String title, String text) {
        tryPing(title, text);
    }

    @SuppressLint("MissingPermission")
    private void tryPing(String title, @Nullable String text) {
        Permissions.withPings(activity, () -> ping(title, text));
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private void ping(String title, @Nullable String text) {
        givePing(Pings.make(activity, PingChannel.COMMAND, title, text, R.drawable.ic_notify),
                 PingChannel.command());
    }
}
