package net.emilla.command.core;

import static android.content.Intent.ACTION_SENDTO;
import static android.content.Intent.ACTION_SEND_MULTIPLE;
import static android.content.Intent.EXTRA_EMAIL;
import static android.content.Intent.EXTRA_STREAM;
import static android.content.Intent.EXTRA_SUBJECT;
import static android.content.Intent.EXTRA_TEXT;

import android.content.Intent;
import android.content.pm.PackageManager;
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
import net.emilla.util.Patterns;

import java.util.ArrayList;

/*internal*/ final class Email extends CoreDataCommand implements EmailReceiver {

    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, new Intent(ACTION_SENDTO, Uri.parse("mailto:")));
    }

    private final ContactEmailsFragment mContactsFragment;
    private final FieldToggle mSubjectToggle;

    /*internal*/ Email(AssistActivity act) {
        super(act, CoreEntry.EMAIL, R.string.data_hint_email);

        mContactsFragment = ContactEmailsFragment.newInstance(true);
        mSubjectToggle = InputField.SUBJECT.toggler(act);

        String entry = CoreEntry.EMAIL.name();
        giveGadgets(
            mContactsFragment,
            mSubjectToggle,
            new FileFetcher(act, entry, "*/*"),
            // TODO: Thunderbird doesn't like certain filetypes. See if you can find a type
            //  statement that's consistently email-friendly.
            new MediaFetcher(act, entry)
        );
    }

    @Override
    protected void run(AssistActivity act) {
        tryEmail(act, "", null);
    }

    @Override
    protected void run(AssistActivity act, String recipients) {
        tryEmail(act, recipients, null);
    }

    @Override
    public void runWithData(AssistActivity act, String body) {
        tryEmail(act, "", body);
    }

    @Override
    public void runWithData(AssistActivity act, String recipients, String body) {
        tryEmail(act, recipients, body);
    }

    private void tryEmail(AssistActivity act, String recipients, @Nullable String body) {
        String addresses = mContactsFragment.selectedContacts();
        if (addresses != null) recipients = addresses;

        email(act, recipients, body);
        // todo: validate the raw recipients
    }

    private void email(AssistActivity act, String addresses, @Nullable String body) {
        ArrayList<Uri> attachments = act.attachments(CoreEntry.EMAIL.name());

        Intent email;
        var sendTo = new Intent(ACTION_SENDTO, Uri.parse("mailto:"));
        if (attachments == null) {
            email = sendTo;
        } else {
            email = new Intent(ACTION_SEND_MULTIPLE).putExtra(EXTRA_STREAM, attachments);
            email.setSelector(sendTo);
        }
        email.putExtra(EXTRA_EMAIL, Patterns.TRIMMING_CSV.split(addresses));
        // TODO: CC and BCC selections

        if (body != null) email.putExtra(EXTRA_TEXT, body);

        String subject = mSubjectToggle.fieldText();
        if (subject != null) email.putExtra(EXTRA_SUBJECT, subject);

        giveApp(act, email);
    }

    @Override
    public void provide(AssistActivity act, String emailAddress) {
        email(act, emailAddress, act.dataText());
    }

}
