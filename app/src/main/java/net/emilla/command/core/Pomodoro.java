package net.emilla.command.core;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;

import net.emilla.Feedback;
import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.command.ActionMap;
import net.emilla.command.Subcommand;
import net.emilla.config.SettingVals;
import net.emilla.event.PingPlan;
import net.emilla.event.PingScheduler;
import net.emilla.event.Plan;
import net.emilla.lang.Lang;
import net.emilla.ping.PingChannel;
import net.emilla.ping.Pings;
import net.emilla.util.Int;
import net.emilla.util.Permission;
import net.emilla.wadget.ActionSurface;

final class Pomodoro extends CoreDataCommand {
    private enum Action {
        WORK,
        BREAK,
    }

    private final ActionMap<Action> mActionMap;
    @Nullable
    private String mWorkMemo;
    @Nullable
    private String mBreakMemo;

    @internal Pomodoro(AssistActivity act) {
        super(act, CoreEntry.POMODORO, R.string.data_hint_pomodoro);

        var res = act.getResources();
        mActionMap = new ActionMap<Action>(res, Action.WORK, Action[]::new);
        mActionMap.put(res, Action.BREAK, R.array.subcmd_pomodoro_break, true);

        var prefs = act.getSharedPreferences();
        mWorkMemo = SettingVals.defaultPomoWorkMemo(prefs, res);
        mBreakMemo = SettingVals.defaultPomoBreakMemo(prefs, res);
    }

    @Override
    protected Feedback run(ActionSurface surface) {
        return tryPomo(surface.getAssistActivity(), null, false);
    }

    @Override
    protected Feedback run(ActionSurface surface, String duration) {
        Subcommand<Action> subcmd = mActionMap.get(duration);
        return tryPomo(surface.getAssistActivity(), subcmd.instruction, subcmd.action == Action.BREAK);
    }

    @Override
    public Feedback runWithData(ActionSurface surface, String memo) {
        mWorkMemo = memo;
        return tryPomo(surface.getAssistActivity(), null, false);
    }

    @Override
    public Feedback runWithData(ActionSurface surface, String duration, String memo) {
        Subcommand<Action> subcmd = mActionMap.get(duration);
        boolean isBreak = subcmd.action == Action.BREAK;
        if (isBreak) {
            mBreakMemo = memo;
        } else {
            mWorkMemo = memo;
        }
        return tryPomo(surface.getAssistActivity(), subcmd.instruction, isBreak);
    }

    @SuppressLint("MissingPermission")
    private Feedback tryPomo(AssistActivity act, @Nullable String duration, boolean isBreak) {
        Int box = durationSeconds(act, duration, isBreak);
        if (box == null) {
            return Feedback.fail(R.string.error_invalid_duration);
        }

        int seconds = box.intValue();
        Permission.PINGS.with(act, () -> pomo(act, seconds, mWorkMemo, mBreakMemo, isBreak));
        return Feedback.silence();
    }

    @Nullable
    private static Int durationSeconds(
        AssistActivity act,
        @Nullable String duration,
        boolean isBreak
    ) {
        if (duration == null) {
            var prefs = act.getSharedPreferences();
            return new Int(
                (isBreak
                    ? SettingVals.defaultPomoBreakMins(prefs)
                    : SettingVals.defaultPomoWorkMins(prefs)
                ) * 60
            );
        }

        return Lang.durationSeconds(act, duration);
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private static void pomo(
        AssistActivity act,
        int seconds,
        String workMemo,
        String breakMemo,
        boolean isBreak
    ) {
        var res = act.getResources();
        if (isBreak) {
            pomo(
                act,
                seconds,
                PingChannel.POMODORO_BREAK_START,
                res.getString(R.string.ping_pomodoro_break),
                breakMemo,
                PingChannel.POMODORO_BREAK_WARNING,
                PingChannel.POMODORO_BREAK_END,
                res.getString(R.string.ping_pomodoro_break_over),
                workMemo
            );
        } else {
            pomo(
                act,
                seconds,
                PingChannel.POMODORO_START,
                res.getString(R.string.ping_pomodoro),
                workMemo,
                PingChannel.POMODORO_WARNING,
                PingChannel.POMODORO_END,
                res.getString(R.string.ping_pomodoro_over),
                breakMemo
            );
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private static void pomo(
        AssistActivity act,
        int seconds,
        PingChannel startChannel,
        String mainTitle,
        String startMemo,
        PingChannel warnChannel,
        PingChannel endChannel,
        String endTitle,
        String endMemo
    ) {
        var res = act.getResources();
        String warnMemo = res.getString(R.string.ping_pomodoro_warn_text);
        var scheduler = new PingScheduler(act);
        if (seconds > 60) {
            givePing(act, startChannel, mainTitle, startMemo);

            scheduler.plan(
                PingPlan.afterSeconds(
                    Plan.POMODORO_WARNING,
                    seconds - 60,
                    makePing(act, warnChannel, mainTitle, warnMemo),
                    warnChannel
                )
            );
        } else {
            givePing(act, warnChannel, mainTitle, warnMemo);
        }

        scheduler.plan(
            PingPlan.afterSeconds(
                Plan.POMODORO_ENDED,
                seconds,
                makePing(act, endChannel, endTitle, endMemo),
                endChannel
            )
        );
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private static void givePing(AssistActivity act, PingChannel channel, String title, String memo) {
        givePing(act, makePing(act, channel, title, memo), channel);
    }

    private static Notification makePing(Context ctx, PingChannel channel, String title, String memo) {
        return Pings.make(ctx, channel, title, memo, R.drawable.ic_pomodoro);
    }
}
