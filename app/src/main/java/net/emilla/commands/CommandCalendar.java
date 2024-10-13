package net.emilla.commands;

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
import net.emilla.exceptions.EmlaAppsException;
import net.emilla.exceptions.EmlaBadCommandException;
import net.emilla.utils.Apps;
import net.emilla.utils.Time;

import java.util.regex.Matcher;

public class CommandCalendar extends CoreDataCommand {
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
private boolean mShowLocationField = false, mShowUrlField = false;

public CommandCalendar(AssistActivity act, String instruct) {
    super(act, instruct, R.string.command_calendar, R.string.instruction_calendar);
}

@Override
public void init() {
    super.init();

    if (mShowLocationField) toggleField(R.id.field_location, R.string.field_location, false);
    giveFieldToggle(R.id.action_field_location, R.string.field_location, R.drawable.ic_location,
            v -> mShowLocationField = toggleField(R.id.field_location, R.string.field_location, true));
    if (mShowUrlField) toggleField(R.id.field_url, R.string.field_url, false);
    giveFieldToggle(R.id.action_field_url, R.string.field_url, R.drawable.ic_web,
            v -> mShowUrlField = toggleField(R.id.field_url, R.string.field_url, true));
}

@Override
public void clean() {
    super.clean();

    removeAction(R.id.action_field_location);
    hideField(R.id.field_location);
    removeAction(R.id.action_field_url);
    hideField(R.id.field_url);
}

private void putTitleAndDate(/*mutable*/ String title) throws EmlaBadCommandException {
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
    String location = fieldText(R.id.field_location);
    if (location != null) mIntent.putExtra(EVENT_LOCATION, location);
    String url = fieldText(R.id.field_url);
    if (url != null) mIntent.putExtra("url", url); // todo: is there a way to query supported extras?
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
