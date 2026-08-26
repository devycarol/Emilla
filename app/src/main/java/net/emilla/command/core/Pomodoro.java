package net.emilla.command.core;

import android.Manifest;
import android.app.Notification;
import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;

import net.emilla.Feedback;
import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.command.EmillaCommand;
import net.emilla.command.ImeAction;
import net.emilla.config.SettingVals;
import net.emilla.datafield.DataDirective;
import net.emilla.datafield.DataSubcommand;
import net.emilla.datafield.Subcommands;
import net.emilla.event.PingPlan;
import net.emilla.event.PingScheduler;
import net.emilla.event.Plan;
import net.emilla.lang.Lang;
import net.emilla.ping.PingChannel;
import net.emilla.ping.Pinger;
import net.emilla.ping.Pings;
import net.emilla.util.Int;
import net.emilla.util.Permission;
import net.emilla.wadget.ActionSurface;

final class Pomodoro extends EmillaCommand {
    private sealed interface PomoAction permits Work, Break {
    }

    private record Work(@Nullable String memo) implements PomoAction {
    }

    private record Break(@Nullable String memo) implements PomoAction {
    }

    private final Subcommands<PomoAction> mSubcommands = new Subcommands<>(
        new DataSubcommand<>(
            Work::new,
            R.drawable.ic_work,
            ImeAction.DO,
            R.string.data_hint_memo
        ),
        new DataSubcommand<>(
            Break::new,
            R.drawable.ic_break,
            ImeAction.DO,
            R.string.data_hint_memo
        )
    );

    @internal Pomodoro(ActionSurface surface) {
        super(surface, CoreEntry.POMODORO);
    }

    @Override
    public DataDirective dataDirective() {
        return mSubcommands;
    }

    @Override
    protected Feedback run(ActionSurface surface) {
        return run(surface, null);
    }

    @Override
    protected Feedback run(ActionSurface surface, @Nullable String duration) {
        var prefs = surface.getSharedPreferences();
        var res = surface.getResources();
        String workMemo;
        String breakMemo;
        boolean isBreak;
        PomoAction pomoAction = mSubcommands.get(surface);
        switch (pomoAction) {
            case Break(@Nullable String memo) -> {
                workMemo = SettingVals.defaultPomoWorkMemo(prefs, res);
                breakMemo = memo != null
                    ? memo
                    : SettingVals.defaultPomoBreakMemo(prefs, res)
                ;
                isBreak = true;
            }
            case Work(@Nullable String memo) -> {
                workMemo = memo != null
                    ? memo
                    : SettingVals.defaultPomoWorkMemo(prefs, res)
                ;
                breakMemo = SettingVals.defaultPomoBreakMemo(prefs, res);
                isBreak = false;
            }
        }
        Int box = durationSeconds(surface, duration, isBreak);
        if (box == null) {
            return Feedback.fail(R.string.error_invalid_duration);
        }

        int seconds = box.intValue();
        var act = surface.getAssistActivity();
        Permission.PINGS.with(act, () -> pomo(act, seconds, workMemo, breakMemo, isBreak));
        return Feedback.silence();
    }

    @Nullable
    private static Int durationSeconds(
        ActionSurface surface,
        @Nullable String duration,
        boolean isBreak
    ) {
        if (duration == null) {
            var prefs = surface.getSharedPreferences();
            return new Int(
                (isBreak
                    ? SettingVals.defaultPomoBreakMins(prefs)
                    : SettingVals.defaultPomoWorkMins(prefs)
                ) * 60
            );
        }

        return Lang.durationSeconds(surface.getContext(), duration);
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private static void pomo(
        AssistActivity act,
        int seconds,
        CharSequence workMemo,
        CharSequence breakMemo,
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
        ActionSurface surface,
        int seconds,
        PingChannel startChannel,
        CharSequence mainTitle,
        CharSequence startMemo,
        PingChannel warnChannel,
        PingChannel endChannel,
        CharSequence endTitle,
        CharSequence endMemo
    ) {
        var ctx = surface.getContext();
        var res = surface.getResources();
        String warnMemo = res.getString(R.string.ping_pomodoro_warn_text);
        var scheduler = new PingScheduler(ctx);
        if (seconds > 60) {
            givePing(surface, startChannel, mainTitle, startMemo);

            scheduler.plan(
                PingPlan.afterSeconds(
                    Plan.POMODORO_WARNING,
                    seconds - 60,
                    makePing(ctx, warnChannel, mainTitle, warnMemo),
                    warnChannel
                )
            );
        } else {
            givePing(surface, warnChannel, mainTitle, warnMemo);
        }

        scheduler.plan(
            PingPlan.afterSeconds(
                Plan.POMODORO_ENDED,
                seconds,
                makePing(ctx, endChannel, endTitle, endMemo),
                endChannel
            )
        );
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private static void givePing(
        ActionSurface surface,
        PingChannel channel,
        CharSequence title,
        CharSequence memo
    ) {
        var ctx = surface.getContext();
        Pinger.of(ctx, makePing(ctx, channel, title, memo), channel).ping();
        surface.take(Feedback.give());
    }

    private static Notification makePing(
        Context ctx,
        PingChannel channel,
        CharSequence title,
        CharSequence memo
    ) {
        return Pings.make(ctx, channel, title, memo, R.drawable.ic_pomodoro);
    }
}
