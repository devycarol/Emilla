package net.emilla.command.core;

import static android.content.Intent.ACTION_SENDTO;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;

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
import net.emilla.util.Uris;

/*internal*/ final class Sms extends CoreDataCommand implements PhoneReceiver {

    public static final String ENTRY = "sms";

    public static boolean possible(PackageManager pm) {
        return Features.sms(pm) || Apps.canDo(pm, Intents.send(Uris.sms("")));
    }

    @Override
    protected boolean shouldLowercase() {
        return false; // the initials "SMS" shouldn't be lowercased
    }

    /*internal*/ Sms(AssistActivity act) {
        super(act, CoreEntry.SMS, R.string.data_hint_message);
    }

    private /*late*/ ContactPhonesFragment mContactsFragment;

    @Override
    protected void init(AssistActivity act, Resources res) {
        super.init(act, res);

        mContactsFragment = ContactPhonesFragment.newInstance(true);

        giveGadgets(mContactsFragment, new MediaFetcher(act, ENTRY));
    }

    @Override
    protected void run() {
        tryMessage(null);
    }

    @Override
    protected void run(String recipients) {
        tryMessage(recipients, null);
    }

    private void tryMessage(@Nullable String message) {
        String numbers = mContactsFragment.selectedContacts();
        if (numbers != null) message(numbers, message);
        else message("", message);
    }

    @Override
    protected void runWithData(String message) {
        tryMessage(message);
    }

    @Override
    protected void runWithData(String recipients, String message) {
        // todo: immediate texting. likely requires a feature check and special permissions.
        //  attachments, feedback for delivered/not delivered..
        tryMessage(recipients, message);
    }

    private void tryMessage(String recipients, @Nullable String message) {
        String numbers = mContactsFragment.selectedContacts();
        if (numbers == null && Contacts.isPhoneNumbers(recipients)) numbers = recipients;

        if (numbers != null) {
            message(numbers, message);
        } else {
            String toNumbers = Contacts.phonewordsToNumbers(recipients);
            String msg = str(R.string.notice_sms_not_numbers, recipients, toNumbers);
            // todo: better message.
            offerDialog(
                Dialogs.dual(
                    this.activity,
                    CoreEntry.SMS.name,
                    msg, R.string.message_directly,

                    (dlg, which) -> message(toNumbers, message)
                )
            );
        }
    }

    private void message(String numbers, @Nullable String message) {
        var sendTo = new Intent(ACTION_SENDTO, Uris.sms(numbers));

        if (message != null) sendTo.putExtra(Intents.EXTRA_SMS_BODY, message);
        // overwrites any existing draft to the recipient
        // Todo: detect, warn, confirm.

        appSucceed(sendTo);
    }

    @Override
    public void provide(String phoneNumber) {
        message(phoneNumber, this.activity.dataText());
    }

}
