package net.emilla.command.core;

import static android.content.Intent.EXTRA_STREAM;
import static android.content.Intent.EXTRA_TEXT;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Intents.Insert;

import androidx.annotation.Nullable;

import net.emilla.Feedback;
import net.emilla.R;
import net.emilla.action.Gadget;
import net.emilla.action.Widget;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.command.ActionMap;
import net.emilla.command.DataField;
import net.emilla.command.EmillaCommand;
import net.emilla.command.Subcommand;
import net.emilla.contact.fragment.ContactCardsFragment;
import net.emilla.content.receive.ContactCardReceiver;
import net.emilla.exception.UnreachableError;
import net.emilla.util.Apps;
import net.emilla.util.Dialogs;
import net.emilla.util.Intents;
import net.emilla.wadget.ActionSurface;

import java.util.List;

final class Contact extends EmillaCommand implements ContactCardReceiver {
    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, Intents.view(Contacts.CONTENT_URI, Contacts.CONTENT_TYPE))
            || Apps.canDo(pm, Intents.edit(Contacts.CONTENT_URI, Contacts.CONTENT_TYPE))
            || Apps.canDo(pm, Intents.send(Contacts.CONTENT_VCARD_TYPE))
            || Apps.canDo(pm, Intents.insert(Contacts.CONTENT_TYPE))
        ;
    }

    private enum Action {
        VIEW,
        EDIT,
        SHARE,
        CREATE,
    }

    private final ContactCardsFragment mContactsFragment = ContactCardsFragment.newInstance();

    private final ActionMap<Action> mActionMap;
    private Action mAction = Action.VIEW;

    @internal Contact(ActionSurface surface) {
        super(
            surface,
            CoreEntry.CONTACT,
            new DataField(R.string.data_hint_contact)
        );
        var res = surface.getResources();
        mActionMap = new ActionMap<Action>(res, Action.VIEW, Action[]::new);

        mActionMap.put(res, Action.VIEW, R.array.subcmd_view, true);
        mActionMap.put(res, Action.EDIT, R.array.subcmd_edit, true);
        mActionMap.put(res, Action.SHARE, R.array.subcmd_share, true);
        mActionMap.put(res, Action.CREATE, R.array.subcmd_create, true);
    }

    @Override
    protected Widget[] widgets() {
        return new Widget[] {
            mContactsFragment,
        };
    }

    @Override
    protected Gadget[] gadgets() {
        return new Gadget[] {
            mContactsFragment,
        };
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
    protected Feedback run(ActionSurface surface) {
        String details = surface.dataText();
        return details != null && mAction != Action.SHARE
            ? create(null, details)
            : Feedback.pend()
        ;
    }

    @Override
    protected Feedback run(ActionSurface surface, String person) {
        String details = surface.dataText();
        if (details != null) {
            return mAction == Action.SHARE
                ? offerCreate(surface, person, details)
                : create(person, details)
            ;
        }

        person = extractAction(person);
        // todo: search by other details as well? nicknames certainly. phones,
        //  addresses, phone types (cell, work, ..) probably, depending on the
        //  command.
        //  special cases:
        //  - me: share your own contact card
        //  - emergency/sos: contact emergency numbers
        return switch (mAction) {
            case VIEW, EDIT, SHARE -> {
                Uri contact = mContactsFragment.selectedContacts();
                if (contact != null) {
                    yield switch (mAction) {
                        case VIEW -> view(contact);
                        case EDIT -> edit(contact);
                        case SHARE -> send(surface.getResources(), contact, null);
                        case CREATE -> throw new UnreachableError();
                    };
                }

                yield person != null
                    ? offerCreate(surface, person, null)
                    : Feedback.pend()
                ;
            }
            case CREATE -> create(person, null);
        };
    }

    private static Feedback view(Uri contact) {
        return Feedback.succeed(Intents.view(contact));
    }

    private static Feedback edit(Uri contact) {
        return Feedback.succeed(Intents.edit(contact));
    }

    private static Feedback send(Resources res, Uri contact, @Nullable String message) {
        // todo: multi-selection for this particular case..
        Intent send = Intents.send(Contacts.CONTENT_VCARD_TYPE);
        List<String> segments = contact.getPathSegments();
        String lookupKey = segments.get(segments.size() - 2);
        Uri vcard = Uri.withAppendedPath(Contacts.CONTENT_VCARD_URI, lookupKey);
        send.putExtra(EXTRA_STREAM, vcard);
        if (message != null) {
            // todo: see if it's possible to detect when apps won't accept this.
            send.putExtra(EXTRA_TEXT, message);
        }
        return Feedback.succeed(Intent.createChooser(send, res.getString(CoreEntry.CONTACT.name)));
    }

    private static Feedback offerCreate(
        ActionSurface surface,
        String person,
        @Nullable String details
    ) {
        var res = surface.getResources();
        String msg = res.getString(R.string.notice_contact_no_match, person);
        return Feedback.offer(
            Dialogs.dual(
                surface.getAssistActivity(),
                CoreEntry.CONTACT.name,
                msg,
                R.string.create,
                (dlg, which) -> {
                    surface.take(create(person, details));
                }
            )
        );
    }

    private static Feedback create(
        @Nullable String person,
        @Nullable String phoneNumber
    ) {
        Intent insert = Intents.insert(Contacts.CONTENT_TYPE);
        if (person != null) {
            insert.putExtra(Insert.NAME, person);
        }
        if (phoneNumber != null) {
            insert.putExtra(Insert.PHONE, phoneNumber);
        }
        // todo: further details. a lot of them..
        return Feedback.succeed(insert);
    }

    @Override
    public void provide(AssistActivity act, Uri contact) {
        act.take(switch (mAction) {
            case VIEW -> view(contact);
            case EDIT -> edit(contact);
            case SHARE -> send(act.getResources(), contact, act.dataText());
            case CREATE -> throw new UnreachableError();
        });
    }
}
