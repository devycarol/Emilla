package net.emilla.command.core;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;

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

final class Pomodoro extends CoreDataCommand {

    private enum Action {
        WORK, BREAK
    }

    private final ActionMap<Action> mActionMap;
    @Nullable
    private String mWorkMemo;
    @Nullable
    private String mBreakMemo;

    /*internal*/ Pomodoro(AssistActivity act) {
        super(act, CoreEntry.POMODORO, R.string.data_hint_pomodoro);

        var res = act.getResources();
        mActionMap = new ActionMap<Action>(res, Action.WORK, Action[]::new);
        mActionMap.put(res, Action.BREAK, R.array.subcmd_pomodoro_break, true);

        var prefs = act.getSharedPreferences();
        mWorkMemo = SettingVals.defaultPomoWorkMemo(prefs, res);
        mBreakMemo = SettingVals.defaultPomoBreakMemo(prefs, res);
    }

    @Override
    protected void run(AssistActivity act) {
        tryPomo(act, null, false);
    }

    @Override
    protected void run(AssistActivity act, String minutes) {
        Subcommand<Action> subcmd = mActionMap.get(minutes);
        tryPomo(act, subcmd.instruction, subcmd.action == Action.BREAK);
    }

    @Override
    public void runWithData(AssistActivity act, String memo) {
        mWorkMemo = memo;
        tryPomo(act, null, false);
    }

    @Override
    public void runWithData(AssistActivity act, String minutes, String memo) {
        Subcommand<Action> subcmd = mActionMap.get(minutes);

        boolean isBreak = subcmd.action == Action.BREAK;
        if (isBreak) mBreakMemo = memo;
        else mWorkMemo = memo;

        tryPomo(act, subcmd.instruction, isBreak);
    }

    @SuppressLint("MissingPermission")
    private void tryPomo(AssistActivity act, @Nullable String minutes, boolean isBreak) {
        int seconds = seconds(act, minutes, isBreak);
        Permission.PINGS.with(act, () -> pomo(act, seconds, mWorkMemo, mBreakMemo, isBreak));
    }

    private static int seconds(AssistActivity act, @Nullable String minutes, boolean isBreak) {
        if (minutes == null) {
            var prefs = act.getSharedPreferences();
            return (
                isBreak
                    ? SettingVals.defaultPomoBreakMins(prefs)
                    : SettingVals.defaultPomoWorkMins(prefs)
            ) * 60;
        }

        return Lang.duration(minutes, CoreEntry.POMODORO.name).seconds;
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private void pomo(
        AssistActivity act,
        int seconds,
        String workMemo,
        String breakMemo,
        boolean isBreak
    ) {
        var res = act.getResources();
        if (isBreak) {
            pomo(
                act, seconds,

                PingChannel.POMODORO_BREAK_START,
                res.getString(R.string.ping_pomodoro_break),
                breakMemo,
                PingChannel.POMODORO_BREAK_WARN,
                PingChannel.POMODORO_BREAK_END,
                res.getString(R.string.ping_pomodoro_break_over),
                workMemo
            );
        } else {
            pomo(
                act, seconds,

                PingChannel.POMODORO_START,
                res.getString(R.string.ping_pomodoro),
                workMemo,
                PingChannel.POMODORO_WARN,
                PingChannel.POMODORO_END,
                res.getString(R.string.ping_pomodoro_over),
                breakMemo
            );
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private void pomo(
        AssistActivity act,
        int seconds,
        String startChannel,
        String mainTitle,
        String startMemo,
        String warnChannel,
        String endChannel,
        String endTitle,
        String endMemo
    ) {
        var res = act.getResources();
        String warnMemo = res.getString(R.string.ping_pomodoro_warn_text);
        var scheduler = new PingScheduler(act);
        if (seconds > 60) {
            givePing(act, startChannel, mainTitle, startMemo);

            scheduler.plan(PingPlan.afterSeconds(
                Plan.POMODORO_WARNING,
                seconds - 60,
                makePing(act, warnChannel, mainTitle, warnMemo),
                warnChannel
            ));

        } else {
            givePing(act, warnChannel, mainTitle, warnMemo);
        }

        scheduler.plan(PingPlan.afterSeconds(
            Plan.POMODORO_ENDED,
            seconds,
            makePing(act, endChannel, endTitle, endMemo),
            endChannel
        ));
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private static void givePing(AssistActivity act, String channel, String title, String memo) {
        givePing(act, makePing(act, channel, title, memo), PingChannel.of(channel));
    }

    private static Notification makePing(Context ctx, String channel, String title, String memo) {
        return Pings.make(ctx, channel, title, memo, R.drawable.ic_pomodoro);
    }

}
