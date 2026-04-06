package net.emilla.command.core;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.CalendarContract.Events;

import androidx.annotation.Nullable;

import net.emilla.R;
import net.emilla.action.field.FieldToggle;
import net.emilla.action.field.InputField;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.util.Apps;
import net.emilla.util.Intents;
import net.emilla.util.MimeTypes;

import java.time.LocalDateTime;
import java.time.ZoneId;

final class Schedule extends CoreDataCommand {
    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, baseIntent());
    }

    // Todo: date/time range widget
    // Todo: all-day toggle
    // Todo: action buttons to select availability, access level, and guests—last requires
    //  contacts stuff. If possible also: reminders, repeats, timezone, event color, and
    //  calendar selection.
    private final FieldToggle mLocation;
    private final FieldToggle mUrl;

    @internal Schedule(AssistActivity act) {
        super(act, CoreEntry.SCHEDULE, R.string.data_hint_schedule);

        mLocation = InputField.LOCATION.toggler(act);
        mUrl = InputField.URL.toggler(act);

        giveGadgets(mLocation, mUrl);
    }

    private static Intent baseIntent() {
        return Intents.insert(Events.CONTENT_URI, MimeTypes.CALENDAR_EVENT);
        // Todo: Etar is broken if already open. May be a flags issue?
    }

    @Override
    protected void run(AssistActivity act) {
        runWithData(act, null, null);
    }

    @Override
    protected void run(AssistActivity act, String title) {
        runWithData(act, title, null);
    }

    @Override
    public void runWithData(AssistActivity act, String details) {
        runWithData(act, null, details);
    }

    @Override
    public void runWithData(AssistActivity act, @Nullable String title, @Nullable String details) {
        Intent intent = baseIntent();
        if (title != null) {
            intent.putExtra(Events.TITLE, title);
        }
        if (details != null) {
            intent.putExtra(Events.DESCRIPTION, details);
        }
        String location = mLocation.fieldText();
        if (location != null) {
            intent.putExtra(Events.EVENT_LOCATION, location);
        }
        String url = mUrl.fieldText();
        if (url != null) {
            intent.putExtra("url", url);
        }
        Apps.succeed(act, intent);
    }

    private static long epochMilliOf(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
