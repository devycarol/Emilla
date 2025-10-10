package net.emilla.command.core;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;

import androidx.annotation.ArrayRes;
import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.command.ActionMap;
import net.emilla.command.Subcommand;
import net.emilla.config.SettingVals;
import net.emilla.event.PingPlan;
import net.emilla.event.PingScheduler;
import net.emilla.event.Plan;
import net.emilla.lang.Lang;
import net.emilla.ping.PingChannel;
import net.emilla.ping.Pings;
import net.emilla.util.Permissions;

public final class Pomodoro extends CoreDataCommand {

    public static final String ENTRY = "pomodoro";
    @StringRes
    public static final int NAME = R.string.command_pomodoro;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_pomodoro;

    public static Yielder yielder() {
        return new Yielder(true, Pomodoro::new, ENTRY, NAME, ALIASES);
    }

    public static boolean possible() {
        return true;
    }

    private enum Action {
        WORK, BREAK
    }

    private ActionMap<Action> mActionMap;
    @Nullable
    private String mWorkMemo, mBreakMemo;

    private Pomodoro(AssistActivity act) {
        super(act, NAME,
              R.string.instruction_pomodoro,
              R.drawable.ic_pomodoro,
              R.string.summary_pomodoro,
              R.string.manual_pomodoro,
              R.string.data_hint_pomodoro);
    }

    @Override @CallSuper
    protected void onInit() {
        super.onInit();

        if (mActionMap == null) {
            mActionMap = new ActionMap<Action>(Action.WORK);
            mActionMap.put(pResources, Action.BREAK, R.array.subcmd_pomodoro_break, true);
        }

        if (mWorkMemo == null || mBreakMemo == null) {
            mWorkMemo = SettingVals.defaultPomoWorkMemo(prefs(), pResources);
            mBreakMemo = SettingVals.defaultPomoBreakMemo(prefs(), pResources);
        }
    }

    @Override @CallSuper
    protected void onClean() {
        super.onClean();

        mWorkMemo = null;
        mBreakMemo = null;
    }

    @Override
    protected void run() {
        tryPomo(null, false);
    }

    @Override
    protected void run(String minutes) {
        Subcommand<Action> subcmd = mActionMap.get(minutes);
        tryPomo(subcmd.instruction, subcmd.action == Action.BREAK);
    }

    @Override
    protected void runWithData(String memo) {
        mWorkMemo = memo;
        tryPomo(null, false);
    }

    @Override
    protected void runWithData(String minutes, String memo) {
        Subcommand<Action> subcmd = mActionMap.get(minutes);

        boolean isBreak = subcmd.action == Action.BREAK;
        if (isBreak) mBreakMemo = memo;
        else mWorkMemo = memo;

        tryPomo(subcmd.instruction, isBreak);
    }

    @SuppressLint("MissingPermission")
    private void tryPomo(@Nullable String minutes, boolean isBreak) {
        int seconds = seconds(minutes, isBreak);
        Permissions.withPings(pActivity, () -> pomo(seconds, mWorkMemo, mBreakMemo, isBreak));
    }

    private int seconds(@Nullable String minutes, boolean isBreak) {
        if (minutes == null) {
            if (isBreak) return SettingVals.defaultPomoBreakMins(prefs()) * 60;
            return SettingVals.defaultPomoWorkMins(prefs()) * 60;
        }

        return Lang.duration(minutes, NAME).seconds;
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private void pomo(int seconds, String workMemo, String breakMemo, boolean isBreak) {
        if (isBreak) {
            pomo(seconds, PingChannel.POMODORO_BREAK_START,
                 str(R.string.ping_pomodoro_break), breakMemo,
                 PingChannel.POMODORO_BREAK_WARN,
                 PingChannel.POMODORO_BREAK_END,
                 str(R.string.ping_pomodoro_break_over), workMemo);
        } else {
            pomo(seconds, PingChannel.POMODORO_START,
                 str(R.string.ping_pomodoro), workMemo,
                 PingChannel.POMODORO_WARN,
                 PingChannel.POMODORO_END,
                 str(R.string.ping_pomodoro_over), breakMemo);
        }

    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private void pomo(
        int seconds,
        String startChannel,
        String mainTitle,
        String startMemo,
        String warnChannel,
        String endChannel,
        String endTitle,
        String endMemo
    ) {
        var scheduler = new PingScheduler(pActivity);
        String warnMemo = str(R.string.ping_pomodoro_warn_text);
        if (seconds > 60) {
            givePing(startChannel, mainTitle, startMemo);

            scheduler.plan(PingPlan.afterSeconds(
                    Plan.POMODORO_WARNING,
                    seconds - 60,
                    makePing(warnChannel, mainTitle, warnMemo),
                    warnChannel));

        } else givePing(warnChannel, mainTitle, warnMemo);

        scheduler.plan(PingPlan.afterSeconds(
                Plan.POMODORO_ENDED,
                seconds,
                makePing(endChannel, endTitle, endMemo),
                endChannel));
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private void givePing(String channel, String title, String memo) {
        givePing(makePing(channel, title, memo), PingChannel.of(channel));
    }

    private Notification makePing(String channel, String title, String memo) {
        return Pings.make(pActivity, channel, title, memo, R.drawable.ic_pomodoro);
    }
}
