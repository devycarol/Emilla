package net.emilla.commands;

import static android.content.Intent.ACTION_INSERT;
import static android.content.Intent.ACTION_PICK;
import static android.content.Intent.ACTION_SEARCH;
import static android.provider.ContactsContract.Intents.Insert.NAME;

import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.ContactsContract.Contacts;

import androidx.annotation.ArrayRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exceptions.EmlaAppsException;
import net.emilla.exceptions.EmlaBadCommandException;
import net.emilla.utils.Apps;

import java.util.regex.Pattern;

public class CommandContact extends CoreDataCommand {
private static final byte
    CONTACT_VIEW = 0,
    CONTACT_CREATE = 1,
    CONTACT_EDIT = 2;

private static byte getContactAction(final String person) { // TODO: lang
    if (person.startsWith("create") || person.startsWith("new")) return CONTACT_CREATE;
    if (person.startsWith("edit")) return CONTACT_EDIT;
    return CONTACT_VIEW;
}

private static String stripContactAction(final String person) { // TODO: lang
    return Pattern.compile("(create|new|edit) *").matcher(person).replaceFirst("");
}

@Override @ArrayRes
public int detailsId() {
    return R.array.details_contact;
}

@Override @StringRes
public int dataHint() {
    return R.string.data_hint_contact;
}

@Override @DrawableRes
public int icon() {
    return R.drawable.ic_contact;
}

@Override
protected void run() {
    if (mChooserIntent.resolveActivity(packageManager()) == null) throw new EmlaAppsException("No contacts app found on your device."); // todo handle at mapping
    offer(mChooserIntent, AssistActivity.PICK_VIEW_CONTACT);
}

private final Intent mChooserIntent = new Intent(ACTION_PICK).setType(Contacts.CONTENT_TYPE);

public CommandContact(final AssistActivity act, final String instruct) {
    super(act, instruct, R.string.command_contact, R.string.instruction_contact);
}

@Override
protected void run(final String person) {
    final PackageManager pm = packageManager();
    byte action = getContactAction(person); // todo: standardize subcommand handling
    switch (action) {
    case CONTACT_VIEW -> {
        final Intent in = Apps.newTask(ACTION_SEARCH, Contacts.CONTENT_TYPE)
                .putExtra(SearchManager.QUERY, person);
        if (in.resolveActivity(pm) == null) {
            // TODO: handle at mapping and fall back to the below search interface if there's no
            //  resolution for the ACTION_SEARCH intent.
            throw new EmlaAppsException("No contact-search app found on your device.");
        }
        succeed(in);
    }
    case CONTACT_CREATE -> {
        final String actualPerson = stripContactAction(person);
        final Intent in = Apps.newTask(ACTION_INSERT, Contacts.CONTENT_TYPE);
        if (!actualPerson.isEmpty()) in.putExtra(NAME, actualPerson);
        // TODO further details
        //  flexibility in where they're put, i.e. "contact new 555-9879"
        //  different phone number types (cell, work, home, etc.)

        if (in.resolveActivity(pm) == null) { // todo handle at mapping
            throw new EmlaAppsException("No contacts app found on your device.");
        }
        succeed(in);
    }
    case CONTACT_EDIT -> {
        final String actualPerson = stripContactAction(person);
        if (actualPerson.isEmpty()) {
            if (mChooserIntent.resolveActivity(pm) == null) {// todo handle at mapping
                throw new EmlaAppsException("No contacts app found on your device.");
            }
            offer(mChooserIntent, AssistActivity.PICK_EDIT_CONTACT);
        } else {
            throw new EmlaBadCommandException("Sorry! I don't know how to search through contacts yet.");
            // TODO: search contacts
            //  no matches: offer to create a contact with the provided details
            //      if declined, return to the command prompt
            //  perfect match: pull up that contact
            //  multiple matches: show a selection dialog
            //  question: how specific to be?
            //      you may or may not want to match against phone numbers 'n stuff.
            //      Searching for a card? Yes. Phone dialer or SMS? No.
            //  special case: me—maybe they want to share their own contact card
        }
    }}
}

@Override
protected void runWithData(final String details) {
    throw new EmlaBadCommandException("Sorry! I can't parse contact info yet."); // TODO
}

@Override
protected void runWithData(final String person, final String details) {
    throw new EmlaBadCommandException("Sorry! I can't parse contact info yet."); // TODO
}
}
