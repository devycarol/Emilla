package net.emilla.command.core;

import static android.content.Intent.EXTRA_STREAM;
import static android.content.Intent.EXTRA_TEXT;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Intents.Insert;

import androidx.annotation.Nullable;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.command.ActionMap;
import net.emilla.command.Subcommand;
import net.emilla.contact.fragment.ContactCardsFragment;
import net.emilla.content.receive.ContactCardReceiver;
import net.emilla.util.Apps;
import net.emilla.util.Dialogs;
import net.emilla.util.Intents;

import java.util.List;

/*internal*/ final class Contact extends CoreDataCommand implements ContactCardReceiver {

    public static final String ENTRY = "contact";

    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, Intents.view(Contacts.CONTENT_URI, Contacts.CONTENT_TYPE))
            || Apps.canDo(pm, Intents.edit(Contacts.CONTENT_URI, Contacts.CONTENT_TYPE))
            || Apps.canDo(pm, Intents.send(Contacts.CONTENT_VCARD_TYPE))
            || Apps.canDo(pm, Intents.insert(Contacts.CONTENT_TYPE));
    }

    private enum Action {
        VIEW, EDIT, SHARE, CREATE
    }

    private final ContactCardsFragment mContactsFragment;

    private final ActionMap<Action> mActionMap;
    private Action mAction = Action.VIEW;

    /*internal*/ Contact(AssistActivity act) {
        super(act, CoreEntry.CONTACT, R.string.data_hint_contact);

        mContactsFragment = ContactCardsFragment.newInstance();

        giveGadgets(mContactsFragment);

        mActionMap = new ActionMap<Action>(Action.VIEW);

        mActionMap.put(this.resources, Action.VIEW, R.array.subcmd_edit, true);
        mActionMap.put(this.resources, Action.EDIT, R.array.subcmd_edit, true);
        mActionMap.put(this.resources, Action.SHARE, R.array.subcmd_share, true);
        mActionMap.put(this.resources, Action.CREATE, R.array.subcmd_create, true);
    }

    @Override
    protected void onInstruct(@Nullable String person) {
        super.onInstruct(extractAction(person));
    }

    @Nullable
    private String extractAction(@Nullable String person) {
        if (person == null) {
            mAction = Action.VIEW;
            return null;
        }

        Subcommand<Action> subcmd = mActionMap.get(person);
        mAction = subcmd.action;

        return subcmd.instruction;
    }

    @Override
    protected void run() {
        this.activity.offerContactCards(this);
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
            } else if (person != null) {
                offerCreate(person, null);
            } else {
                this.activity.offerContactCards(this);
            }
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
        if (mAction != Action.SHARE) {
            create(person, details);
        } else if (person != null) {
            offerCreate(person, details);
        } else {
            this.activity.offerContactCards(this);
        }
    }

    private void view(Uri contact) {
        appSucceed(Intents.view(contact));
    }

    private void edit(Uri contact) {
        appSucceed(Intents.edit(contact));
    }

    private void send(Uri contact, @Nullable String message) {
        // todo: multi-selection for this particular case..
        Intent send = Intents.send(Contacts.CONTENT_VCARD_TYPE);

        List<String> segments = contact.getPathSegments();
        String lookupKey = segments.get(segments.size() - 2);
        Uri vcard = Uri.withAppendedPath(Contacts.CONTENT_VCARD_URI, lookupKey);
        send.putExtra(EXTRA_STREAM, vcard);

        if (message != null) send.putExtra(EXTRA_TEXT, message);
        // todo: see if it's possible to detect when apps won't accept this.

        appSucceed(Intent.createChooser(send, str(CoreEntry.CONTACT.name)));
    }

    private void offerCreate(String person, @Nullable String details) {
        String msg = str(R.string.notice_contact_no_match, person);
        offerDialog(Dialogs.dual(this.activity, CoreEntry.CONTACT.name, msg, R.string.create,
                                 (dlg, which) -> create(person, details)));
    }

    private void create(@Nullable String person, @Nullable String phoneNumber) {
        Intent insert = Intents.insert(Contacts.CONTENT_TYPE);
        if (person != null) insert.putExtra(Insert.NAME, person);
        if (phoneNumber != null) insert.putExtra(Insert.PHONE, phoneNumber);
        // todo: further details. a lot of them..
        appSucceed(insert);
    }

    @Override
    public void provide(Uri contact) {
        switch (mAction) {
        case EDIT -> edit(contact);
        case SHARE -> send(contact, this.activity.dataText());
        default -> view(contact);
        }
    }

}
