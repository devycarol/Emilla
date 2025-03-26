package net.emilla.command.core;

import static android.content.Intent.EXTRA_STREAM;
import static android.content.Intent.EXTRA_TEXT;

import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Intents.Insert;

import androidx.annotation.ArrayRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.command.ActionMap;
import net.emilla.command.Subcommand;
import net.emilla.contact.fragment.ContactCardsFragment;
import net.emilla.content.receive.ContactCardReceiver;
import net.emilla.settings.Aliases;
import net.emilla.app.Apps;
import net.emilla.util.Dialogs;

import java.util.List;

public final class Contact extends CoreDataCommand implements ContactCardReceiver {

    public static final String ENTRY = "contact";
    @StringRes
    public static final int NAME = R.string.command_contact;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_contact;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static Yielder yielder() {
        return new Yielder(true, Contact::new, ENTRY, NAME, ALIASES);
    }

    private enum Action {
        VIEW, EDIT, SHARE, CREATE
    }

    private ContactCardsFragment mContactsFragment;

    private ActionMap<Action> mActionMap;
    private Action mAction = Action.VIEW;

    public Contact(AssistActivity act) {
        super(act, NAME,
              R.string.instruction_contact,
              R.drawable.ic_contact,
              R.string.summary_contact,
              R.string.manual_contact,
              R.string.data_hint_contact);
    }

    @Override
    protected void onInit() {
        super.onInit();

        if (mContactsFragment == null) mContactsFragment = ContactCardsFragment.newInstance();
        activity.giveActionBox(mContactsFragment);

        if (mActionMap == null) {
            mActionMap = new ActionMap<>(Action.VIEW);

            mActionMap.put(resources, Action.VIEW, R.array.subcmd_edit, true);
            mActionMap.put(resources, Action.EDIT, R.array.subcmd_edit, true);
            mActionMap.put(resources, Action.SHARE, R.array.subcmd_share, true);
            mActionMap.put(resources, Action.CREATE, R.array.subcmd_create, true);
        }
    }

    @Override
    protected void onInstruct(@Nullable String instruction) {
        super.onInstruct(instruction);
        mContactsFragment.search(mAction == Action.CREATE ? null : extractAction(instruction));
        // Todo: maybe don't show contacts for the 'create' subcommand
    }

    @Override
    protected void onClean() {
        super.onClean();

        activity.removeActionBox(mContactsFragment);
        mContactsFragment = null;
    }

    @Nullable
    private String extractAction(@Nullable String person) {
        if (person == null) {
            mAction = Action.VIEW;
            return null;
        }

        Subcommand<Action> subcmd = mActionMap.get(person);
        mAction = subcmd.action();

        return subcmd.instruction();
    }

    @Override
    protected void run() {
        activity.offerContactCards(this);
    }

    @Override
    protected void run(String person) {
        contact(extractAction(person));
    }

    private void contact(@Nullable String person) {
        // todo: search by other details as well? nicknames certainly. phones, addresses, phone
        //  types (cell, work, ..) probably, depending on the command.
        //  special cases:
        //  - me: share your own contact card
        //  - emergency/sos: contact emergency numbers (SOS could be its own command, "panic button")
        //    - see calyx's panic button functionality
        switch (mAction) {
        case VIEW, EDIT, SHARE -> {
            Uri contact = mContactsFragment.selectedContacts();
            if (contact != null) switch (mAction) {
                case VIEW -> view(contact);
                case EDIT -> edit(contact);
                case SHARE -> send(contact, null);
            } else if (person != null) offerCreate(person, null);
            else activity.offerContactCards(this);
        }
        case CREATE -> create(person, null);
        }
    }

    @Override
    protected void runWithData(String details) {
        // TODO LANG: only show data field in 'create' or 'send' mode.
        contact(null, details);
    }

    @Override
    protected void runWithData(String person, String details) {
        // TODO LANG: only show data field in 'create' or 'send' mode.
        contact(person, details);
    }

    private void contact(@Nullable String person, String details) {
        // Todo: dynamic data hint
        if (mAction != Action.SHARE) create(person, details);
        else if (person != null) offerCreate(person, details);
        else activity.offerContactCards(this);
    }

    private void view(Uri contact) {
        appSucceed(Apps.viewTask(contact));
    }

    private void edit(Uri contact) {
        appSucceed(Apps.editTask(contact));
    }

    private void send(Uri contact, @Nullable String message) {
        // todo: multi-selection for this particular case..
        Intent send = Apps.sendTask(Contacts.CONTENT_VCARD_TYPE);

        List<String> segments = contact.getPathSegments();
        String lookupKey = segments.get(segments.size() - 2);
        Uri vcard = Uri.withAppendedPath(Contacts.CONTENT_VCARD_URI, lookupKey);
        send.putExtra(EXTRA_STREAM, vcard);

        if (message != null) send.putExtra(EXTRA_TEXT, message);
        // todo: see if it's possible to detect when apps won't accept this.

        appSucceed(Intent.createChooser(send, str(NAME)));
    }

    private void offerCreate(String person, @Nullable String details) {
        var msg = str(R.string.notice_contact_no_match, person);
        offerDialog(Dialogs.dual(activity, NAME, msg, R.string.create,
                (dlg, which) -> create(person, details)));
    }

    private void create(@Nullable String person, @Nullable String phoneNumber) {
        Intent insert = Apps.insertTask(Contacts.CONTENT_TYPE);
        if (person != null) insert.putExtra(Insert.NAME, person);
        if (phoneNumber != null) insert.putExtra(Insert.PHONE, phoneNumber);
        // todo: further details. a lot of them..
        appSucceed(insert);
    }

    @Override
    public void provide(Uri contact) {
        switch (mAction) {
        case EDIT -> edit(contact);
        case SHARE -> send(contact, activity.dataText());
        default -> view(contact);
        }
    }
}
