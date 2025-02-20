package net.emilla.command.core;

import static android.provider.CalendarContract.EXTRA_EVENT_ALL_DAY;
import static android.provider.CalendarContract.EXTRA_EVENT_BEGIN_TIME;
import static android.provider.CalendarContract.EXTRA_EVENT_END_TIME;
import static android.provider.CalendarContract.Events.DESCRIPTION;
import static android.provider.CalendarContract.Events.EVENT_LOCATION;
import static android.provider.CalendarContract.Events.TITLE;
import static java.util.regex.Pattern.CASE_INSENSITIVE;

import android.content.Intent;
import android.provider.CalendarContract.Events;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.action.field.FieldToggle;
import net.emilla.action.field.LocationField;
import net.emilla.action.field.UrlField;
import net.emilla.exception.EmlaBadCommandException;
import net.emilla.settings.Aliases;
import net.emilla.util.Apps;
import net.emilla.util.Time;

import java.util.regex.Pattern;

public final class Calendar extends CoreDataCommand {

    public static final String ENTRY = "calendar";
    @StringRes
    public static final int NAME = R.string.command_calendar;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_calendar;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static Yielder yielder() {
        return new Yielder(true, Calendar::new, ENTRY, NAME, ALIASES);
    }

    private static final class CalendarParams extends CoreDataParams {

        private CalendarParams() {
            super(NAME,
                  R.string.instruction_calendar,
                  R.drawable.ic_calendar,
                  R.string.summary_calendar,
                  R.string.manual_calendar,
                  R.string.data_hint_calendar);
        }
    }

    // Todo: Etar is broken if already open. May be a flags issue?
    private final Intent mIntent = Apps.insertTask(Events.CONTENT_URI, "vnd.android.cursor.dir/event");
    private FieldToggle mLocationToggle, mUrlToggle;

    public Calendar(AssistActivity act) {
        super(act, new CalendarParams());
    }

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

    private void putTitleAndDate(String title) throws EmlaBadCommandException {
        // todo: clean this up
        var m = Pattern.compile(" */all(day)?", CASE_INSENSITIVE).matcher(title);
        if (m.find()) {
            title = m.replaceFirst("");
            mIntent.putExtra(EXTRA_EVENT_ALL_DAY, true);
        }
        var nameAndTime = title.split(" *\\| *");
        switch (nameAndTime.length) {
        case 1 -> {}
        case 2 -> {
            title = nameAndTime[0];
            long[] times = Time.parseDateAndTimes(nameAndTime[1]);
            mIntent.putExtra(EXTRA_EVENT_BEGIN_TIME, times[0]);
            if (times[1] != 0) mIntent.putExtra(EXTRA_EVENT_END_TIME, times[1]);
        }
        default -> throw badCommand(R.string.error_multiple_dates);
        }
        if (!title.isEmpty()) mIntent.putExtra(TITLE, title);
    }

    @Override
    protected void run() {
        String location = mLocationToggle.fieldText();
        if (location != null) mIntent.putExtra(EVENT_LOCATION, location);
        String url = mUrlToggle.fieldText();
        if (url != null) mIntent.putExtra("url", url);
        // todo: is there a way to query supported extras?
        // Todo: action buttons to select availability, access level, and guests—last requires contacts
        //  stuff. If possible also: reminders, repeats, timezone, event color, and calendar selection.
        appSucceed(mIntent);
        // todo: etar calendar acts really janky in the recents which causes unwanted event-saving
        // it also flashes white on start even in the dark (black, LineageOS) theme
    }

    @Override
    protected void run(@NonNull String titleAndDate) {
        putTitleAndDate(titleAndDate);
        run();
    }

    @Override
    protected void runWithData(@NonNull String details) {
        mIntent.putExtra(DESCRIPTION, details);
        run();
    }

    @Override
    protected void runWithData(@NonNull String titleAndDate, @NonNull String details) {
        putTitleAndDate(titleAndDate);
        mIntent.putExtra(DESCRIPTION, details);
        run();
    }
}
