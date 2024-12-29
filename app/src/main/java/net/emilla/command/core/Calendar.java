package net.emilla.command.core;

import static android.content.Intent.ACTION_INSERT;
import static android.provider.CalendarContract.EXTRA_EVENT_ALL_DAY;
import static android.provider.CalendarContract.EXTRA_EVENT_BEGIN_TIME;
import static android.provider.CalendarContract.EXTRA_EVENT_END_TIME;
import static android.provider.CalendarContract.Events.DESCRIPTION;
import static android.provider.CalendarContract.Events.EVENT_LOCATION;
import static android.provider.CalendarContract.Events.TITLE;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

import android.content.Intent;
import android.provider.CalendarContract.Events;

import androidx.annotation.ArrayRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.action.field.FieldToggle;
import net.emilla.action.field.LocationField;
import net.emilla.action.field.UrlField;
import net.emilla.exceptions.EmlaAppsException;
import net.emilla.exceptions.EmlaBadCommandException;
import net.emilla.utils.Apps;
import net.emilla.utils.Time;

import java.util.regex.Matcher;

public class Calendar extends CoreDataCommand {

    @Override @ArrayRes
    public int detailsId() {
        return R.array.details_calendar;
    }

    @Override @StringRes
    public int dataHint() {
        return R.string.data_hint_calendar;
    }

    @Override @DrawableRes
    public int icon() {
        return R.drawable.ic_calendar;
    }

    // Todo: Etar is broken if already open. May be a flags issue?
    private final Intent mIntent = Apps.newTask(ACTION_INSERT, Events.CONTENT_URI, "vnd.android.cursor.dir/event");
    private FieldToggle mLocationToggle, mUrlToggle;

    public Calendar(AssistActivity act, String instruct) {
        super(act, instruct, R.string.command_calendar, R.string.instruction_calendar);
    }

    @Override
    public void init() {
        super.init();

        AssistActivity act = activity();
        if (mLocationToggle == null) mLocationToggle = new LocationField(act);
        else if (mLocationToggle.activated()) reshowField(LocationField.FIELD_ID);
        giveAction(mLocationToggle);
        if (mUrlToggle == null) mUrlToggle = new UrlField(act);
        else if (mUrlToggle.activated()) reshowField(UrlField.FIELD_ID);
        giveAction(mUrlToggle);
    }

    @Override
    public void clean() {
        super.clean();

        removeAction(LocationField.ACTION_ID);
        hideField(LocationField.FIELD_ID);
        removeAction(UrlField.ACTION_ID);
        hideField(UrlField.FIELD_ID);
    }

    private void putTitleAndDate(String title) throws EmlaBadCommandException {
        // todo: clean this up
        Matcher m = compile(" */all(day)?", CASE_INSENSITIVE).matcher(title);
        if (m.find()) {
            title = m.replaceFirst("");
            mIntent.putExtra(EXTRA_EVENT_ALL_DAY, true);
        }
        String[] nameAndTime = title.split(" *\\| *");
        switch (nameAndTime.length) {
        case 1 -> {}
        case 2 -> {
            title = nameAndTime[0];
            long[] times = Time.parseDateAndTimes(nameAndTime[1]);
            mIntent.putExtra(EXTRA_EVENT_BEGIN_TIME, times[0]);
            if (times[1] != 0) mIntent.putExtra(EXTRA_EVENT_END_TIME, times[1]);
        }
        default -> throw new EmlaBadCommandException("You can't have multiple dates.");
        }
        if (!title.isEmpty()) mIntent.putExtra(TITLE, title);
    }

    @Override
    protected void run() {
        if (mIntent.resolveActivity(packageManager()) == null) throw new EmlaAppsException("No calendar app found on your device."); // todo: handle at mapping
        String location = mLocationToggle.fieldText();
        if (location != null) mIntent.putExtra(EVENT_LOCATION, location);
        String url = mUrlToggle.fieldText();
        if (url != null) mIntent.putExtra("url", url);
        // todo: is there a way to query supported extras?
        // Todo: action buttons to select availability, access level, and guests—last requires contacts
        //  stuff. If possible also: reminders, repeats, timezone, event color, and calendar selection.
        succeed(mIntent);
        // todo: etar calendar acts really janky in the recents which causes unwanted event-saving
        // it also flashes white on start even in the dark (black, LineageOS) theme
    }

    @Override
    protected void run(String titleAndDate) {
        putTitleAndDate(titleAndDate);
        run();
    }

    @Override
    protected void runWithData(String details) {
        mIntent.putExtra(DESCRIPTION, details);
        run();
    }

    @Override
    protected void runWithData(String titleAndDate, String details) {
        putTitleAndDate(titleAndDate);
        mIntent.putExtra(DESCRIPTION, details);
        run();
    }
}
