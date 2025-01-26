package net.emilla.command.core;

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
import net.emilla.settings.Aliases;
import net.emilla.util.Apps;
import net.emilla.util.Dialogs;

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

    @Override
    protected void run() {
        activity.offerContactCards(this);
    }

    @Override
    protected void run(@NonNull String person) {
        contact(extractAction(person));
    }

    @Override
    protected void runWithData(@NonNull String details) {
        // TODO LANG: only show data field in 'create' mode.
        create(null, details);
    }

    @Override
    protected void runWithData(@NonNull String person, @NonNull String details) {
        // TODO LANG: only show data field in 'create' mode.
        create(person, details);
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

    private void contact(@Nullable String person) {
        switch (mAction) {
        case VIEW, EDIT -> {
            if (person != null) offerCreate(person);
            // TODO: search contacts
            //  no matches: offer to create a contact with the provided details
            //      if declined, return to the command prompt
            //  perfect match: pull up that contact
            //  multiple matches: show a selection dialog
            //  question: how specific to be?
            //      you may or may not want to match against phone numbers 'n stuff.
            //  special case: me—maybe they want to share their own contact card
            else activity.offerContactCards(this);
        }
        case CREATE -> create(person, null);
        }
    }

    private void view(@NonNull Uri contact) {
        appSucceed(Apps.viewTask(contact));
    }

    private void edit(@NonNull Uri contact) {
        appSucceed(Apps.editTask(contact));
    }

    private void offerCreate(@NonNull String person) {
        String msg = string(R.string.notice_contact_no_match, person);
        offerDialog(Dialogs.dual(activity, NAME, msg, R.string.create,
                (dlg, which) -> create(person, null)));
    }

    private void create(@Nullable String person, @Nullable String phoneNumber) {
        Intent insert = Apps.insertTask(Contacts.CONTENT_TYPE);
        if (person != null) insert.putExtra(Insert.NAME, person);
        if (phoneNumber != null) insert.putExtra(Insert.PHONE, phoneNumber);
        // todo: further details. a lot of them..
        appSucceed(insert);
    }

    @Override
    public void provide(@NonNull Uri contact) {
        switch (mAction) {
        case EDIT -> edit(contact);
        default -> view(contact);
        }
    }
}
