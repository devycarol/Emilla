package net.emilla.command.core;

import static android.provider.CalendarContract.EXTRA_EVENT_ALL_DAY;
import static android.provider.CalendarContract.EXTRA_EVENT_BEGIN_TIME;
import static android.provider.CalendarContract.EXTRA_EVENT_END_TIME;
import static android.provider.CalendarContract.Events.DESCRIPTION;
import static android.provider.CalendarContract.Events.EVENT_LOCATION;
import static android.provider.CalendarContract.Events.TITLE;
import static java.util.regex.Pattern.CASE_INSENSITIVE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.CalendarContract.Events;

import net.emilla.R;
import net.emilla.action.field.FieldToggle;
import net.emilla.action.field.InputField;
import net.emilla.activity.AssistActivity;
import net.emilla.lang.date.DateTimeSpan;
import net.emilla.lang.date.Time;
import net.emilla.util.Apps;
import net.emilla.util.Intents;
import net.emilla.util.MimeTypes;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class Calendar extends CoreDataCommand {

    private static final Pattern TAG_ALL_DAY = Pattern.compile(" */all(day)?", CASE_INSENSITIVE);
    private static final Pattern TRIMMING_PSV = Pattern.compile(" *\\| *");
    // TODO LANG: please stop.

    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, makeIntent());
    }

    private final FieldToggle mLocationToggle;
    private final FieldToggle mUrlToggle;

    /*internal*/ Calendar(AssistActivity act) {
        super(act, CoreEntry.CALENDAR, R.string.data_hint_calendar);

        mLocationToggle = InputField.LOCATION.toggler(act);
        mUrlToggle = InputField.URL.toggler(act);

        giveGadgets(mLocationToggle, mUrlToggle);
    }

    @Override
    protected void run(AssistActivity act) {
        calendar(act, makeIntent());
    }

    @Override
    protected void run(AssistActivity act, String titleAndDate) {
        calendar(act, makeIntent(titleAndDate));
    }

    @Override
    public void runWithData(AssistActivity act, String details) {
        calendar(act, makeIntent().putExtra(DESCRIPTION, details));
    }

    @Override
    public void runWithData(AssistActivity act, String titleAndDate, String details) {
        calendar(act, makeIntent(titleAndDate).putExtra(DESCRIPTION, details));
    }

    private static Intent makeIntent() {
        return Intents.insert(Events.CONTENT_URI, MimeTypes.CALENDAR_EVENT);
        // Todo: Etar is broken if already open. May be a flags issue?
    }

    private Intent makeIntent(String titleAndDate) {
        Intent intent = makeIntent();

        String title = titleAndDate;
        Matcher m = TAG_ALL_DAY.matcher(title);
        if (m.find()) {
            title = m.replaceFirst("");
            intent.putExtra(EXTRA_EVENT_ALL_DAY, true);
        }

        String[] nameAndTime = TRIMMING_PSV.split(title);
        switch (nameAndTime.length) {
        case 1 -> {}
        case 2 -> {
            title = nameAndTime[0];
            DateTimeSpan times = Time.parseDateAndTimes(nameAndTime[1], CoreEntry.CALENDAR.name);

            intent.putExtra(EXTRA_EVENT_BEGIN_TIME, epochMilliOf(times.start));

            LocalDateTime end = times.end;
            if (end != null) {
                intent.putExtra(EXTRA_EVENT_END_TIME, epochMilliOf(end));
            }
        }
        default -> throw badCommand(R.string.error_invalid_date);
        }

        if (!title.isEmpty()) {
            intent.putExtra(TITLE, title);
        }

        return intent;
    }

    private static long epochMilliOf(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    private void calendar(AssistActivity act, Intent intent) {
        String location = mLocationToggle.fieldText();
        if (location != null) intent.putExtra(EVENT_LOCATION, location);

        String url = mUrlToggle.fieldText();
        if (url != null) intent.putExtra("url", url);

        // todo: is there a way to query supported extras?
        // Todo: action buttons to select availability, access level, and guests—last requires
        //  contacts stuff. If possible also: reminders, repeats, timezone, event color, and
        //  calendar selection.
        appSucceed(act, intent);
        // todo: etar calendar acts janky in the recents
    }

}
