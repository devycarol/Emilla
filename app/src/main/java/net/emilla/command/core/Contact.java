package net.emilla.command.core;

import static android.content.Intent.ACTION_SEARCH;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Intents.Insert;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.content.receive.ContactCardReceiver;
import net.emilla.exception.EmlaBadCommandException;
import net.emilla.settings.Aliases;
import net.emilla.util.Apps;

public class Contact extends CoreDataCommand implements ContactCardReceiver {

    public static final String ENTRY = "contact";
    @StringRes
    public static final int NAME = R.string.command_contact;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_contact;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static Yielder yielder() {
        return new Yielder(true, Contact::new, ENTRY, NAME, ALIASES);
    }

    private static final byte
            VIEW = 0,
            CREATE = 1,
            EDIT = 2;

    private static class ContactParams extends CoreDataParams {

        private ContactParams() {
            super(NAME,
                  R.string.instruction_contact,
                  R.drawable.ic_contact,
                  R.string.summary_contact,
                  R.string.manual_contact,
                  R.string.data_hint_contact);
        }
    }

    private byte mAction;

    public Contact(AssistActivity act) {
        super(act, new ContactParams());
    }

    @Nullable
    private String extractAction(String person) {
        // TODO LANG: replace with tri-state button
        if (person == null) return null;

        String lcPerson = person.toLowerCase();
        if (lcPerson.startsWith("create")) {
            mAction = CREATE;
            person = person.substring(6).trim();
        } else if (lcPerson.startsWith("new")) {
            mAction = CREATE;
            person = person.substring(3).trim();
        } else if (lcPerson.startsWith("edit")) {
            mAction = EDIT;
            person = person.substring(4).trim();
        } else mAction = VIEW;

        return person.isEmpty() ? null : person;
    }

    @Override
    protected void run() {
        activity.offerContactCards(this);
    }

    @Override
    protected void run(@NonNull String person) {
        person = extractAction(person);
        switch (mAction) {
        case VIEW -> {
            appSucceed(new Intent(ACTION_SEARCH).setType(Contacts.CONTENT_TYPE)
                    .putExtra(SearchManager.QUERY, person));
            // Todo: fall back to the search interface if there's no ACTION_SEARCH app.
        }
        case CREATE -> {
            Intent in = Apps.insertTask(Contacts.CONTENT_TYPE);
            if (!person.isEmpty()) in.putExtra(Insert.NAME, person);
            // Todo: further details
            //  flexibility in where they're put, i.e. "contact new 555-9879"
            //  different phone number types (cell, work, home, etc.)

            appSucceed(in);
        }
        case EDIT -> {
            if (person.isEmpty()) activity.offerContactCards(this);
            else throw new EmlaBadCommandException(NAME, R.string.error_unfinished_contact_search);
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
    protected void runWithData(@NonNull String details) {
        throw new EmlaBadCommandException(NAME, R.string.error_unfinished_contact_details); // TODO
    }

    @Override
    protected void runWithData(@NonNull String person, @NonNull String details) {
        throw new EmlaBadCommandException(NAME, R.string.error_unfinished_contact_details); // TODO
    }

    @Override
    public void provide(@NonNull Uri contact) {
        appSucceed(mAction == EDIT ? Apps.editTask(contact) : Apps.viewTask(contact));
    }
}
