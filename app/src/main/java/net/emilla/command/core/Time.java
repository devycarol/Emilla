package net.emilla.command.core;

import android.content.Context;
import android.content.res.Resources;
import android.view.inputmethod.EditorInfo;

import net.emilla.Feedback;
import net.emilla.R;
import net.emilla.annotation.internal;
import net.emilla.command.EmillaCommand;
import net.emilla.lang.Lang;
import net.emilla.time.TimeZone;
import net.emilla.wadget.ActionSurface;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

final class Time extends EmillaCommand {
    @internal Time(Context ctx) {
        super(ctx, CoreEntry.TIME, EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected Feedback run(ActionSurface surface) {
        return giveTime(surface.getResources(), TimeZone.LOCAL);
    }

    @Override
    protected Feedback run(ActionSurface surface, String location) {
        var zone = TimeZone.of(Lang.EN_US, location);
        if (zone == null) {
            return Feedback.fail(R.string.error_invalid_time_zone);
        }

        return giveTime(surface.getResources(), zone);
    }

    private static Feedback giveTime(Resources res, TimeZone zone) {
        var now = LocalTime.now(zone.id());
        var formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
        String time = now.format(formatter);
        var zoneName = res.getString(zone.name);
        return Feedback.giveText(res.getString(R.string.zoned_time, time, zoneName));
    }
}
