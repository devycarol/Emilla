package net.emilla.command.core;

import static android.content.Intent.ACTION_SENDTO;

import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.Nullable;

import net.emilla.Feedback;
import net.emilla.R;
import net.emilla.action.Gadget;
import net.emilla.action.MediaFetcher;
import net.emilla.action.Widget;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.command.DataField;
import net.emilla.command.EmillaCommand;
import net.emilla.contact.fragment.ContactPhonesFragment;
import net.emilla.content.receive.PhoneReceiver;
import net.emilla.util.Apps;
import net.emilla.util.Contacts;
import net.emilla.util.Dialogs;
import net.emilla.util.Features;
import net.emilla.util.Intents;
import net.emilla.util.Strings;
import net.emilla.util.Uris;
import net.emilla.wadget.ActionSurface;

final class Sms extends EmillaCommand implements PhoneReceiver {
    public static boolean possible(PackageManager pm) {
        return Features.sms(pm) || Apps.canDo(pm, Intents.send(Uris.sms("")));
    }

    private final ContactPhonesFragment mContactsFragment
        = ContactPhonesFragment.newInstance(true)
    ;
    private final MediaFetcher mMediaFetcher;

    @internal Sms(ActionSurface surface) {
        super(
            surface,
            CoreEntry.SMS,
            new DataField(R.string.data_hint_message)
        );
        mMediaFetcher = new MediaFetcher(
            surface.getAssistActivity(),
            CoreEntry.SMS.name()
        );
    }

    @Override
    protected Widget[] widgets() {
        return new Widget[] {
            mContactsFragment,
            mMediaFetcher,
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
        String numbers = mContactsFragment.selectedContacts();
        String message = surface.dataText();
        return message(Strings.emptyIfNull(numbers), message);
    }

    @Override
    protected Feedback run(ActionSurface surface, String recipients) {
        // todo: immediate texting. likely requires a feature check and special permissions.
        //  attachments, feedback for delivered/not delivered..
        AssistActivity act = surface.getAssistActivity();
        String numbers = mContactsFragment.selectedContacts();
        String message = surface.dataText();
        if (numbers == null && Contacts.isPhoneNumbers(recipients)) {
            numbers = recipients;
        }
        if (numbers != null) {
            return message(numbers, message);
        }

        var res = act.getResources();
        String toNumbers = Contacts.phonewordsToNumbers(recipients);
        String msg = res.getString(R.string.notice_sms_not_numbers, recipients, toNumbers);
        // todo: better message.
        return Feedback.offer(
            Dialogs.dual(
                act,
                CoreEntry.SMS.name,
                msg,
                R.string.message_directly,
                (dlg, which) -> act.take(message(toNumbers, message))
            )
        );
    }

    private static Feedback message(String numbers, @Nullable String message) {
        var sendTo = new Intent(ACTION_SENDTO, Uris.sms(numbers));
        if (message != null) {
            sendTo.putExtra(Intents.EXTRA_SMS_BODY, message);
        }
        // overwrites any existing draft to the recipient
        // Todo: detect, warn, confirm.
        return Feedback.succeed(sendTo);
    }

    @Override
    public void provide(AssistActivity act, String phoneNumber) {
        act.take(message(phoneNumber, act.dataText()));
    }
}
