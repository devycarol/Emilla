package net.emilla.command.core;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.CalendarContract.Events;

import androidx.annotation.Nullable;

import net.emilla.Feedback;
import net.emilla.R;
import net.emilla.action.InputField;
import net.emilla.action.box.FieldsFragment;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.util.Apps;
import net.emilla.util.Intents;
import net.emilla.util.MimeTypes;
import net.emilla.wadget.ActionSurface;

import java.time.LocalDateTime;
import java.time.ZoneId;

final class Schedule extends CoreDataCommand {
    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, baseIntent());
    }

    // Todo: date/time range widget
    // Todo: all-day toggle
    // Todo: action buttons to select availability, access level, and
    //  guests—requires contacts stuff. If possible also: reminders, repeats,
    //  timezone, event color, and calendar selection.
    private final FieldsFragment mFieldsFragment = FieldsFragment.newInstance(
        InputField.URL,
        InputField.LOCATION
    );

    @internal Schedule(AssistActivity act) {
        super(act, CoreEntry.SCHEDULE, R.string.data_hint_schedule);

        giveGadgets(mFieldsFragment);
    }

    private static Intent baseIntent() {
        return Intents.insert(Events.CONTENT_URI, MimeTypes.CALENDAR_EVENT);
        // Todo: Etar is broken if already open. May be a flags issue?
    }

    @Override
    protected Feedback run(ActionSurface surface, AssistActivity act) {
        return runWithData(surface, act, null, null);
    }

    @Override
    protected Feedback run(ActionSurface surface, AssistActivity act, String title) {
        return runWithData(surface, act, title, null);
    }

    @Override
    public Feedback runWithData(ActionSurface surface, AssistActivity act, String details) {
        return runWithData(surface, act, null, details);
    }

    @Override
    public Feedback runWithData(ActionSurface surface, AssistActivity act, @Nullable String title, @Nullable String details) {
        Intent intent = baseIntent();
        if (title != null) {
            intent.putExtra(Events.TITLE, title);
        }
        if (details != null) {
            intent.putExtra(Events.DESCRIPTION, details);
        }
        String location = mFieldsFragment.get(InputField.LOCATION);
        if (location != null) {
            intent.putExtra(Events.EVENT_LOCATION, location);
        }
        String url = mFieldsFragment.get(InputField.URL);
        if (url != null) {
            intent.putExtra("url", url);
        }
        return Feedback.succeed(intent);
    }

    private static long epochMilliOf(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
