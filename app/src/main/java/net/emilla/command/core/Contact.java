package net.emilla.command.core;

import static android.content.Intent.EXTRA_STREAM;
import static android.content.Intent.EXTRA_TEXT;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Intents.Insert;

import androidx.annotation.Nullable;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.command.ActionMap;
import net.emilla.command.Subcommand;
import net.emilla.contact.fragment.ContactCardsFragment;
import net.emilla.content.receive.ContactCardReceiver;
import net.emilla.util.Apps;
import net.emilla.util.Dialogs;
import net.emilla.util.Intents;

import java.util.List;

final class Contact extends CoreDataCommand implements ContactCardReceiver {

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

    @internal Contact(Context ctx) {
        super(ctx, CoreEntry.CONTACT, R.string.data_hint_contact);

        mContactsFragment = ContactCardsFragment.newInstance();

        giveGadgets(mContactsFragment);

        var res = ctx.getResources();
        mActionMap = new ActionMap<Action>(res, Action.VIEW, Action[]::new);

        mActionMap.put(res, Action.VIEW, R.array.subcmd_view, true);
        mActionMap.put(res, Action.EDIT, R.array.subcmd_edit, true);
        mActionMap.put(res, Action.SHARE, R.array.subcmd_share, true);
        mActionMap.put(res, Action.CREATE, R.array.subcmd_create, true);
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
    protected void run(AssistActivity act) {
        act.offerContactCards(this);
    }

    @Override
    protected void run(AssistActivity act, String person) {
        contact(act, extractAction(person));
    }

    private void contact(AssistActivity act, @Nullable String person) {
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
                case VIEW -> view(act, contact);
                case EDIT -> edit(act, contact);
                case SHARE -> send(act, contact, null);
            } else if (person != null) {
                offerCreate(act, person, null);
            } else {
                act.offerContactCards(this);
            }
        }
        case CREATE -> create(act, person, null);
        }
    }

    @Override
    public void runWithData(AssistActivity act, String details) {
        // TODO LANG: only show data field in 'create' or 'send' mode.
        contact(act, null, details);
    }

    @Override
    public void runWithData(AssistActivity act, String person, String details) {
        // TODO LANG: only show data field in 'create' or 'send' mode.
        contact(act, person, details);
    }

    private void contact(AssistActivity act, @Nullable String person, String details) {
        // Todo: dynamic data hint
        if (mAction != Action.SHARE) {
            create(act, person, details);
        } else if (person != null) {
            offerCreate(act, person, details);
        } else {
            act.offerContactCards(this);
        }
    }

    private static void view(AssistActivity act, Uri contact) {
        appSucceed(act, Intents.view(contact));
    }

    private static void edit(AssistActivity act, Uri contact) {
        appSucceed(act, Intents.edit(contact));
    }

    private void send(AssistActivity act, Uri contact, @Nullable String message) {
        // todo: multi-selection for this particular case..
        Intent send = Intents.send(Contacts.CONTENT_VCARD_TYPE);

        List<String> segments = contact.getPathSegments();
        String lookupKey = segments.get(segments.size() - 2);
        Uri vcard = Uri.withAppendedPath(Contacts.CONTENT_VCARD_URI, lookupKey);
        send.putExtra(EXTRA_STREAM, vcard);

        if (message != null) send.putExtra(EXTRA_TEXT, message);
        // todo: see if it's possible to detect when apps won't accept this.

        var res = act.getResources();
        appSucceed(act, Intent.createChooser(send, res.getString(CoreEntry.CONTACT.name)));
    }

    private void offerCreate(AssistActivity act, String person, @Nullable String details) {
        var res = act.getResources();
        String msg = res.getString(R.string.notice_contact_no_match, person);
        offerDialog(
            act,
            Dialogs.dual(
                act, CoreEntry.CONTACT.name,

                msg, R.string.create,

                (dlg, which) -> create(act, person, details)
            )
        );
    }

    private static void create(
        AssistActivity act,
        @Nullable String person,
        @Nullable String phoneNumber
    ) {
        Intent insert = Intents.insert(Contacts.CONTENT_TYPE);
        if (person != null) insert.putExtra(Insert.NAME, person);
        if (phoneNumber != null) insert.putExtra(Insert.PHONE, phoneNumber);
        // todo: further details. a lot of them..
        appSucceed(act, insert);
    }

    @Override
    public void provide(AssistActivity act, Uri contact) {
        switch (mAction) {
        case EDIT -> edit(act, contact);
        case SHARE -> send(act, contact, act.dataText());
        default -> view(act, contact);
        }
    }

}
