package net.emilla.command.core;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.res.Resources;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;

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
import net.emilla.util.Permission;

/*internal*/ final class Pomodoro extends CoreDataCommand {

    public static final String ENTRY = "pomodoro";

    public static boolean possible() {
        return true;
    }

    private enum Action {
        WORK, BREAK
    }

    /*internal*/ Pomodoro(AssistActivity act) {
        super(act, CoreEntry.POMODORO, R.string.data_hint_pomodoro);
    }

    private /*late*/ ActionMap<Action> mActionMap;
    @Nullable
    private /*late*/ String mWorkMemo;
    @Nullable
    private /*late*/ String mBreakMemo;

    @Override
    protected void init(AssistActivity act, Resources res) {
        super.init(act, res);

        mActionMap = new ActionMap<Action>(Action.WORK);
        mActionMap.put(res, Action.BREAK, R.array.subcmd_pomodoro_break, true);

        mWorkMemo = SettingVals.defaultPomoWorkMemo(prefs(), res);
        mBreakMemo = SettingVals.defaultPomoBreakMemo(prefs(), res);
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
        Permission.PINGS.with(this.activity, () -> pomo(seconds, mWorkMemo, mBreakMemo, isBreak));
    }

    private int seconds(@Nullable String minutes, boolean isBreak) {
        if (minutes == null) {
            return (
                isBreak
                    ? SettingVals.defaultPomoBreakMins(prefs())
                    : SettingVals.defaultPomoWorkMins(prefs())
            ) * 60;
        }

        return Lang.duration(minutes, CoreEntry.POMODORO.name).seconds;
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private void pomo(int seconds, String workMemo, String breakMemo, boolean isBreak) {
        if (isBreak) {
            pomo(
                seconds,
                PingChannel.POMODORO_BREAK_START,
                str(R.string.ping_pomodoro_break),
                breakMemo,
                PingChannel.POMODORO_BREAK_WARN,
                PingChannel.POMODORO_BREAK_END,
                str(R.string.ping_pomodoro_break_over),
                workMemo
            );
        } else {
            pomo(
                seconds,
                PingChannel.POMODORO_START,
                str(R.string.ping_pomodoro),
                workMemo,
                PingChannel.POMODORO_WARN,
                PingChannel.POMODORO_END,
                str(R.string.ping_pomodoro_over),
                breakMemo
            );
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
        var scheduler = new PingScheduler(this.activity);
        String warnMemo = str(R.string.ping_pomodoro_warn_text);
        if (seconds > 60) {
            givePing(startChannel, mainTitle, startMemo);

            scheduler.plan(PingPlan.afterSeconds(
                Plan.POMODORO_WARNING,
                seconds - 60,
                makePing(warnChannel, mainTitle, warnMemo),
                warnChannel
            ));

        } else {
            givePing(warnChannel, mainTitle, warnMemo);
        }

        scheduler.plan(PingPlan.afterSeconds(
            Plan.POMODORO_ENDED,
            seconds,
            makePing(endChannel, endTitle, endMemo),
            endChannel
        ));
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private void givePing(String channel, String title, String memo) {
        givePing(makePing(channel, title, memo), PingChannel.of(channel));
    }

    private Notification makePing(String channel, String title, String memo) {
        return Pings.make(this.activity, channel, title, memo, R.drawable.ic_pomodoro);
    }

}
