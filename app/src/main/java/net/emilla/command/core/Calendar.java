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

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.action.field.FieldToggle;
import net.emilla.action.field.LocationField;
import net.emilla.action.field.UrlField;
import net.emilla.activity.AssistActivity;
import net.emilla.app.Apps;
import net.emilla.util.Time;

import java.util.regex.Pattern;

public final class Calendar extends CoreDataCommand {

    public static final String ENTRY = "calendar";
    @StringRes
    public static final int NAME = R.string.command_calendar;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_calendar;

    public static Yielder yielder() {
        return new Yielder(true, Calendar::new, ENTRY, NAME, ALIASES);
    }

    public static boolean possible(PackageManager pm) {
        return canDo(pm, makeIntent());
    }

    private Calendar(AssistActivity act) {
        super(act, NAME,
              R.string.instruction_calendar,
              R.drawable.ic_calendar,
              R.string.summary_calendar,
              R.string.manual_calendar,
              R.string.data_hint_calendar);
    }

    private FieldToggle mLocationToggle, mUrlToggle;

    @Override
    protected void onInit() {
        super.onInit();

        if (mLocationToggle == null) mLocationToggle = new LocationField(activity);
        else if (mLocationToggle.activated()) reshowField(LocationField.FIELD_ID);
        giveAction(mLocationToggle);
        if (mUrlToggle == null) mUrlToggle = new UrlField(activity);
        else if (mUrlToggle.activated()) reshowField(UrlField.FIELD_ID);
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
        var m = Pattern.compile(" */all(day)?", CASE_INSENSITIVE).matcher(titleAndDate);
        if (m.find()) {
            titleAndDate = m.replaceFirst("");
            intent.putExtra(EXTRA_EVENT_ALL_DAY, true);
        }
        var nameAndTime = titleAndDate.split(" *\\| *");
        switch (nameAndTime.length) {
        case 1 -> {}
        case 2 -> {
            titleAndDate = nameAndTime[0];
            long[] times = Time.parseDateAndTimes(nameAndTime[1], NAME);
            intent.putExtra(EXTRA_EVENT_BEGIN_TIME, times[0]);
            if (times[1] != 0) intent.putExtra(EXTRA_EVENT_END_TIME, times[1]);
        }
        default -> throw badCommand(R.string.error_multiple_dates);
        }
        if (!titleAndDate.isEmpty()) intent.putExtra(TITLE, titleAndDate);

        return intent;
    }

    private void calendar(Intent intent) {
        String location = mLocationToggle.fieldText();
        if (location != null) intent.putExtra(EVENT_LOCATION, location);
        String url = mUrlToggle.fieldText();
        if (url != null) intent.putExtra("url", url);
        // todo: is there a way to query supported extras?
        // Todo: action buttons to select availability, access level, and guests—last requires contacts
        //  stuff. If possible also: reminders, repeats, timezone, event color, and calendar selection.
        appSucceed(intent);
        // todo: etar calendar acts really janky in the recents which causes unwanted event-saving
        // it also flashes white on start even in the dark (LineageOS black) theme
    }
}
