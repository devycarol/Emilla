package net.emilla.command.core;

import static android.content.Intent.ACTION_SENDTO;

import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.Nullable;

import net.emilla.R;
import net.emilla.action.MediaFetcher;
import net.emilla.activity.AssistActivity;
import net.emilla.contact.fragment.ContactPhonesFragment;
import net.emilla.content.receive.PhoneReceiver;
import net.emilla.util.Apps;
import net.emilla.util.Contacts;
import net.emilla.util.Dialogs;
import net.emilla.util.Features;
import net.emilla.util.Intents;
import net.emilla.util.Strings;
import net.emilla.util.Uris;

/*internal*/ final class Sms extends CoreDataCommand implements PhoneReceiver {

    public static boolean possible(PackageManager pm) {
        return Features.sms(pm) || Apps.canDo(pm, Intents.send(Uris.sms("")));
    }

    private final ContactPhonesFragment mContactsFragment;

    /*internal*/ Sms(AssistActivity act) {
        super(act, CoreEntry.SMS, R.string.data_hint_message);

        mContactsFragment = ContactPhonesFragment.newInstance(true);

        giveGadgets(mContactsFragment, new MediaFetcher(act, CoreEntry.SMS.name()));
    }

    @Override
    protected void run(AssistActivity act) {
        tryMessage(act, null);
    }

    @Override
    protected void run(AssistActivity act, String recipients) {
        tryMessage(act, recipients, null);
    }

    private void tryMessage(AssistActivity act, @Nullable String message) {
        String numbers = mContactsFragment.selectedContacts();
        message(act, Strings.emptyIfNull(numbers), message);
    }

    @Override
    public void runWithData(AssistActivity act, String message) {
        tryMessage(act, message);
    }

    @Override
    public void runWithData(AssistActivity act, String recipients, String message) {
        // todo: immediate texting. likely requires a feature check and special permissions.
        //  attachments, feedback for delivered/not delivered..
        tryMessage(act, recipients, message);
    }

    private void tryMessage(AssistActivity act, String recipients, @Nullable String message) {
        String numbers = mContactsFragment.selectedContacts();
        if (numbers == null && Contacts.isPhoneNumbers(recipients)) numbers = recipients;

        if (numbers != null) {
            message(act, numbers, message);
        } else {
            var res = act.getResources();

            String toNumbers = Contacts.phonewordsToNumbers(recipients);
            String msg = res.getString(R.string.notice_sms_not_numbers, recipients, toNumbers);
            // todo: better message.
            offerDialog(
                act,
                Dialogs.dual(
                    act, CoreEntry.SMS.name,

                    msg, R.string.message_directly,

                    (dlg, which) -> message(act, toNumbers, message)
                )
            );
        }
    }

    private static void message(AssistActivity act, String numbers, @Nullable String message) {
        var sendTo = new Intent(ACTION_SENDTO, Uris.sms(numbers));

        if (message != null) sendTo.putExtra(Intents.EXTRA_SMS_BODY, message);
        // overwrites any existing draft to the recipient
        // Todo: detect, warn, confirm.

        appSucceed(act, sendTo);
    }

    @Override
    public void provide(AssistActivity act, String phoneNumber) {
        message(act, phoneNumber, act.dataText());
    }

}
