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
import net.emilla.action.field.LocationField;
import net.emilla.action.field.UrlField;
import net.emilla.activity.AssistActivity;
import net.emilla.apps.Apps;
import net.emilla.lang.date.Time;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*internal*/ final class Calendar extends CoreDataCommand {

    public static final String ENTRY = "calendar";

    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, makeIntent());
    }

    /*internal*/ Calendar(AssistActivity act) {
        super(act, CoreEntry.CALENDAR, R.string.data_hint_calendar);
    }

    private FieldToggle mLocationToggle = null;
    private FieldToggle mUrlToggle = null;

    @Override
    protected void onInit() {
        super.onInit();

        if (mLocationToggle == null) {
            mLocationToggle = new LocationField(this.activity);
        } else if (mLocationToggle.activated()) {
            reshowField(LocationField.FIELD_ID);
        }
        giveAction(mLocationToggle);

        if (mUrlToggle == null) {
            mUrlToggle = new UrlField(this.activity);
        } else if (mUrlToggle.activated()) {
            reshowField(UrlField.FIELD_ID);
        }
        giveAction(mUrlToggle);
    }

    @Override
    protected void onClean() {
        super.onClean();

        removeAction(LocationField.ACTION_ID);
        hideField(LocationField.FIELD_ID);
        removeAction(UrlField.ACTION_ID);
        hideField(UrlField.FIELD_ID);
    }

    @Override
    protected void run() {
        calendar(makeIntent());
    }

    @Override
    protected void run(String titleAndDate) {
        calendar(makeIntent(titleAndDate));
    }

    @Override
    protected void runWithData(String details) {
        calendar(makeIntent().putExtra(DESCRIPTION, details));
    }

    @Override
    protected void runWithData(String titleAndDate, String details) {
        calendar(makeIntent(titleAndDate).putExtra(DESCRIPTION, details));
    }

    private static Intent makeIntent() {
        return Apps.insertTask(Events.CONTENT_URI, "vnd.android.cursor.dir/event");
        // Todo: Etar is broken if already open. May be a flags issue?
    }

    private Intent makeIntent(String titleAndDate) {
        Intent intent = makeIntent();

        // todo: clean this up
        var p = Pattern.compile(" */all(day)?", CASE_INSENSITIVE);
        // TODO LANG
        Matcher m = p.matcher(titleAndDate);
        if (m.find()) {
            titleAndDate = m.replaceFirst("");
            intent.putExtra(EXTRA_EVENT_ALL_DAY, true);
        }
        String[] nameAndTime = titleAndDate.split(" *\\| *");
        switch (nameAndTime.length) {
        case 1 -> {}
        case 2 -> {
            titleAndDate = nameAndTime[0];
            long[] times = Time.parseDateAndTimes(nameAndTime[1], CoreEntry.CALENDAR.name);
            intent.putExtra(EXTRA_EVENT_BEGIN_TIME, times[0]);
            if (times[1] != 0L) {
                intent.putExtra(EXTRA_EVENT_END_TIME, times[1]);
            }
        }
        default -> throw badCommand(R.string.error_multiple_dates);
        }

        if (!titleAndDate.isEmpty()) {
            intent.putExtra(TITLE, titleAndDate);
        }

        return intent;
    }

    private void calendar(Intent intent) {
        String location = mLocationToggle.fieldText();
        if (location != null) intent.putExtra(EVENT_LOCATION, location);

        String url = mUrlToggle.fieldText();
        if (url != null) intent.putExtra("url", url);

        // todo: is there a way to query supported extras?
        // Todo: action buttons to select availability, access level, and guests—last requires
        //  contacts stuff. If possible also: reminders, repeats, timezone, event color, and
        //  calendar selection.
        appSucceed(intent);
        // todo: etar calendar acts janky in the recents
    }

}
