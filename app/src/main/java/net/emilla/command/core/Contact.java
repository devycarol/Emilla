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
import net.emilla.command.EmillaCommand;
import net.emilla.command.ImeAction;
import net.emilla.contact.fragment.ContactCardsFragment;
import net.emilla.content.receive.ContactCardReceiver;
import net.emilla.datafield.DataDirective;
import net.emilla.datafield.DataSubcommand;
import net.emilla.datafield.IdSubcommand;
import net.emilla.datafield.Subcommands;
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

    private sealed interface ContactAction permits Id, Create, Share {
    }

    private enum Id implements ContactAction {
        VIEW,
        EDIT,
    }

    private record Create(@Nullable String details)
        implements ContactAction
    {
    }

    private record Share(@Nullable String message) implements ContactAction {
    }

    private final ContactCardsFragment mContactsFragment
        = ContactCardsFragment.newInstance()
    ;
    private final Subcommands<ContactAction> mSubcommands = new Subcommands<>(
        new IdSubcommand<>(Id.VIEW, R.drawable.ic_view, ImeAction.GO),
        new DataSubcommand<>(
            Create::new,
            R.drawable.ic_add,
            ImeAction.GO,
            R.string.data_hint_contact
        ),
        new IdSubcommand<>(Id.EDIT, R.drawable.ic_edit, ImeAction.GO),
        new DataSubcommand<>(
            Share::new,
            R.drawable.ic_share,
            ImeAction.SEND,
            R.string.data_hint_message
        )
    );

    @internal Contact(ActionSurface surface) {
        super(surface, CoreEntry.CONTACT);
    }

    @Override
    public DataDirective dataDirective() {
        return mSubcommands;
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

    @Override
    protected Feedback run(ActionSurface surface) {
        return run(surface, null);
    }

    @Override
    protected Feedback run(ActionSurface surface, @Nullable String person) {
        // todo: search by other details as well? nicknames certainly. phones,
        //  addresses, phone types (cell, work, ..) probably, depending on the
        //  command.
        //  special cases:
        //  - me: share your own contact card
        //  - emergency/sos: contact emergency numbers
        ContactAction subcommand = mSubcommands.get(surface);
        return switch (subcommand) {
            case Id id -> switch (id) {
                // Todo: watch for the stupid error requiring this nested switch
                //  to be fixed
                case VIEW -> {
                    Uri contact = mContactsFragment.selectedContacts();
                    yield contact != null ? view(contact)
                        : person != null ? offerCreate(surface, person, null)
                        : Feedback.pend()
                    ;
                }
                case EDIT -> {
                    Uri contact = mContactsFragment.selectedContacts();
                    yield contact != null ? edit(contact)
                        : person != null ? offerCreate(surface, person, null)
                        : Feedback.pend()
                    ;
                }
            };
            case Create(@Nullable String details) -> create(person, details);
            case Share(@Nullable String message) -> {
                Uri contact = mContactsFragment.selectedContacts();
                yield contact != null ? send(
                    surface.getResources(),
                    contact,
                    message
                )
                    : person != null ? offerCreate(surface, person, null)
                    : Feedback.pend()
                ;
            }
        };
    }

    private static Feedback view(Uri contact) {
        return Feedback.succeed(Intents.view(contact));
    }

    private static Feedback edit(Uri contact) {
        return Feedback.succeed(Intents.edit(contact));
    }

    private static Feedback send(
        Resources res,
        Uri contact,
        @Nullable String message
    ) {
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
        Feedback feedback;
        ContactAction contactAction = mSubcommands.get(act);
        if (contactAction == Id.VIEW) {
            feedback = view(contact);
        } else if (contactAction == Id.EDIT) {
            feedback = edit(contact);
        } else if (contactAction instanceof Create) {
            feedback = edit(contact);
        } else if (contactAction instanceof Share(@Nullable String message)) {
            feedback = send(act.getResources(), contact, message);
        } else {
            throw new UnreachableError();
        }
        act.take(feedback);
    }
}
