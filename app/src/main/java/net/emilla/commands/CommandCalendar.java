package net.emilla.commands;

import static android.content.Intent.ACTION_INSERT;
import static android.content.Intent.EXTRA_EMAIL;
import static android.provider.CalendarContract.EXTRA_EVENT_ALL_DAY;
import static android.provider.CalendarContract.EXTRA_EVENT_BEGIN_TIME;
import static android.provider.CalendarContract.EXTRA_EVENT_END_TIME;
import static android.provider.CalendarContract.Events.*;
import static java.lang.Character.isWhitespace;
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
import net.emilla.parsing.CalendarDetailParsing;
import net.emilla.parsing.TimeParsing;
import net.emilla.utils.Apps;
import net.emilla.utils.Tags;

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

private final Intent mIntent = Apps.newTask(ACTION_INSERT, Events.CONTENT_URI, "vnd.android.cursor.dir/event");
private String mDetails;

public CommandCalendar(final AssistActivity act) {
    super(act, R.string.command_calendar, R.string.instruction_calendar);
}

private void putTitleAndDate(/*mutable*/ String title) throws EmlaBadCommandException {
    // todo: clean this up
    final Matcher m = compile(" */all(day)?", CASE_INSENSITIVE).matcher(title);
    if (m.find()) {
        title = m.replaceFirst("");
        mIntent.putExtra(EXTRA_EVENT_ALL_DAY, true);
    }
    final String[] nameAndTime = title.split(" *\\| *");
    switch (nameAndTime.length) {
    case 1 -> {}
    case 2 -> {
        title = nameAndTime[0];
        long[] times = TimeParsing.parseDateAndTimes(nameAndTime[1]);
        mIntent.putExtra(EXTRA_EVENT_BEGIN_TIME, times[0]);
        if (times[1] != 0) mIntent.putExtra(EXTRA_EVENT_END_TIME, times[1]);
    }
    default -> throw new EmlaBadCommandException("You can't have multiple dates.");
    }
    if (!title.isEmpty()) mIntent.putExtra(TITLE, title);
}

private void clearDetails() {
    mIntent.removeExtra(EVENT_LOCATION);
    mIntent.removeExtra(EXTRA_EMAIL);
    mIntent.removeExtra("url");
    mIntent.removeExtra(AVAILABILITY);
    mIntent.removeExtra(ACCESS_LEVEL);
    mIntent.removeExtra(DESCRIPTION);
}

private void putDetails(final String details) {
    mDetails = details;
    mDetails = Tags.putIfPresent(mDetails, Tags.LOCATION, mIntent, EVENT_LOCATION, Tags.LOCATION);
    if (Tags.itHas(mDetails, Tags.GUESTS)) {
        final String guests = Tags.getFrom(mDetails, Tags.GUESTS, Tags.CALENDAR_TAGS);
        if (guests.matches("(\\S+@\\S+\\.\\S+,)*\\S+@\\S+\\.\\S+")) mDetails = Tags.strip(mDetails, Tags.GUESTS, guests);
        else {
            clearDetails();
            throw new EmlaBadCommandException("Sorry! 'Guests' only supports email contact(s)."); // TODO: have the ui assist with this instead
        }
        mIntent.putExtra(EXTRA_EMAIL, guests); // TODO: contact-parse the CSV-email string. also huh that regex?
    }
    mDetails = Tags.putIfPresent(mDetails, Tags.URL, mIntent, "url", Tags.CALENDAR_TAGS); // todo: is there a way to query supported extras?
    if (Tags.itHas(mDetails, Tags.AVAIL)) {
        final String availability = Tags.getFrom(mDetails, Tags.AVAIL, Tags.CALENDAR_TAGS);
        mDetails = Tags.strip(mDetails, Tags.AVAIL, availability);
        mIntent.putExtra(AVAILABILITY, CalendarDetailParsing.parseAvailability(availability));
    }
    if (Tags.itHas(mDetails, Tags.ACCESS)) {
        final String visibility = Tags.getFrom(mDetails, Tags.ACCESS, Tags.CALENDAR_TAGS);
        mDetails = Tags.strip(mDetails, Tags.ACCESS, visibility);
        mIntent.putExtra(ACCESS_LEVEL, CalendarDetailParsing.parseVisibility(visibility));
    }
    if (Tags.itHas(mDetails, Tags.DETAILS)) {
        final String description = Tags.getFrom(mDetails, Tags.DETAILS, Tags.CALENDAR_TAGS);
        mDetails = Tags.strip(mDetails, Tags.DETAILS, description);
        if (mDetails.isEmpty()) mDetails = description;
        else if (isWhitespace(mDetails.charAt(mDetails.length() - 1))) mDetails += description;
        else mDetails = mDetails + ' ' + description;
        mIntent.putExtra(DESCRIPTION, mDetails.trim());
    } else if (!mDetails.isEmpty()) mIntent.putExtra(DESCRIPTION, mDetails.trim());
}

@Override
public void run() {
    if (mIntent.resolveActivity(packageManager()) == null) throw new EmlaAppsException("No calendar app found on your device."); // todo: handle at mapping
    succeed(mIntent);
    // todo: etar calendar acts really janky in the recents which causes unwanted event-saving
    // it also flashes white on start even in the dark (black, LineageOS) theme
}

@Override
public void run(final String titleAndDate) {
    putTitleAndDate(titleAndDate);
    run();
}

@Override
public void runWithData(final String details) {
    putDetails(details);
    run();
}

@Override
public void runWithData(final String titleAndDate, final String details) {
    putTitleAndDate(titleAndDate);
    putDetails(details);
    run();
}
}
