package net.emilla.command.core;

import static android.content.Intent.ACTION_SENDTO;
import static android.content.Intent.ACTION_SEND_MULTIPLE;
import static android.content.Intent.EXTRA_EMAIL;
import static android.content.Intent.EXTRA_STREAM;
import static android.content.Intent.EXTRA_SUBJECT;
import static android.content.Intent.EXTRA_TEXT;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;

import androidx.annotation.Nullable;

import net.emilla.R;
import net.emilla.action.FileFetcher;
import net.emilla.action.MediaFetcher;
import net.emilla.action.field.FieldToggle;
import net.emilla.action.field.InputField;
import net.emilla.activity.AssistActivity;
import net.emilla.contact.fragment.ContactEmailsFragment;
import net.emilla.content.receive.EmailReceiver;
import net.emilla.util.Apps;

import java.util.ArrayList;

/*internal*/ final class Email extends CoreDataCommand implements EmailReceiver {

    public static final String ENTRY = "email";

    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, new Intent(ACTION_SENDTO, Uri.parse("mailto:")));
    }

    /*internal*/ Email(AssistActivity act) {
        super(act, CoreEntry.EMAIL, R.string.data_hint_email);
    }

    private /*late*/ ContactEmailsFragment mContactsFragment;
    private /*late*/ FieldToggle mSubjectToggle;

    @Override
    protected void init(AssistActivity act, Resources res) {
        super.init(act, res);

        mContactsFragment = ContactEmailsFragment.newInstance(true);
        mSubjectToggle = InputField.SUBJECT.toggler(act);

        giveGadgets(
            mContactsFragment,
            mSubjectToggle,
            new FileFetcher(act, ENTRY, "*/*"),
            // TODO: Thunderbird doesn't like certain filetypes. See if you can find a type
            //  statement that's consistently email-friendly.
            new MediaFetcher(act, ENTRY)
        );
    }

    @Override
    protected void run() {
        tryEmail("", null);
    }

    @Override
    protected void run(String recipients) {
        tryEmail(recipients, null);
    }

    @Override
    protected void runWithData(String body) {
        tryEmail("", body);
    }

    @Override
    protected void runWithData(String recipients, String body) {
        tryEmail(recipients, body);
    }

    private void tryEmail(String recipients, @Nullable String body) {
        String addresses = mContactsFragment.selectedContacts();
        if (addresses != null) recipients = addresses;

        email(recipients, body);
        // todo: validate the raw recipients
    }

    private void email(String addresses, @Nullable String body) {
        ArrayList<Uri> attachments = this.activity.attachments(ENTRY);

        Intent email;
        var sendTo = new Intent(ACTION_SENDTO, Uri.parse("mailto:"));
        if (attachments == null) {
            email = sendTo;
        } else {
            email = new Intent(ACTION_SEND_MULTIPLE).putExtra(EXTRA_STREAM, attachments);
            email.setSelector(sendTo);
        }
        email.putExtra(EXTRA_EMAIL, addresses.split(", *"));
        // TODO: CC and BCC selections

        if (body != null) email.putExtra(EXTRA_TEXT, body);

        String subject = mSubjectToggle.fieldText();
        if (subject != null) email.putExtra(EXTRA_SUBJECT, subject);

        giveApp(email);
    }

    @Override
    public void provide(String emailAddress) {
        email(emailAddress, this.activity.dataText());
    }

}
