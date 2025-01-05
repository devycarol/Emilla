package net.emilla.command.core;

import static android.content.Intent.ACTION_SEARCH;
import static android.provider.ContactsContract.Intents.Insert.NAME;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;

import androidx.annotation.ArrayRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.content.receive.ContactReceiver;
import net.emilla.exception.EmlaBadCommandException;
import net.emilla.utils.Apps;

public class Contact extends CoreDataCommand implements ContactReceiver {

    private static final byte
            VIEW = 0,
            CREATE = 1,
            EDIT = 2;

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

    private byte mAction;

    public Contact(AssistActivity act, String instruct) {
        super(act, instruct, R.string.command_contact, R.string.instruction_contact);
    }

    private String extractAction(String person) {
        // TODO LANG: replace with tri-state button
        if (person.startsWith("create")) {
            mAction = CREATE;
            return person.substring(6).trim();
        }
        if (person.startsWith("new")) {
            mAction = CREATE;
            return person.substring(3).trim();
        }
        if (person.startsWith("edit")) {
            mAction = EDIT;
            return person.substring(5).trim();
        }
        mAction = VIEW;
        return person;
    }

    @Override
    protected void run() {
        activity.offerContacts(this);
    }

    @Override
    protected void run(String person) {
        person = extractAction(person);
        switch (mAction) {
        case VIEW -> {
            appSucceed(new Intent(ACTION_SEARCH).setType(Contacts.CONTENT_TYPE)
                    .putExtra(SearchManager.QUERY, person));
            // Todo: fall back to the search interface if there's no ACTION_SEARCH app.
        }
        case CREATE -> {
            Intent in = Apps.insertTask(Contacts.CONTENT_TYPE);
            if (!person.isEmpty()) in.putExtra(NAME, person);
            // Todo: further details
            //  flexibility in where they're put, i.e. "contact new 555-9879"
            //  different phone number types (cell, work, home, etc.)

            appSucceed(in);
        }
        case EDIT -> {
            if (person.isEmpty()) activity.offerContacts(this);
            else throw new EmlaBadCommandException(R.string.command_contact, R.string.error_unfinished_contact_search);
            // Todo: search contacts
            //  no matches: offer to create a contact with the provided details
            //      if declined, return to the command prompt
            //  perfect match: pull up that contact
            //  multiple matches: show a selection dialog
            //  question: how specific to be?
            //      you may or may not want to match against phone numbers 'n stuff.
            //      Searching for a card? Yes. Phone dialer or SMS? No.
            //  special case: me—maybe they want to share their own contact card
        }}
    }

    @Override
    protected void runWithData(String details) {
        throw new EmlaBadCommandException(R.string.command_contact, R.string.error_unfinished_contact_details); // TODO
    }

    @Override
    protected void runWithData(String person, String details) {
        throw new EmlaBadCommandException(R.string.command_contact, R.string.error_unfinished_contact_details); // TODO
    }

    @Override
    public void provide(Uri contact) {
        appSucceed(mAction == EDIT ? Apps.editTask(contact) : Apps.viewTask(contact));
    }
}
