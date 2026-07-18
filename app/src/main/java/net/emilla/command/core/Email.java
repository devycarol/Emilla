package net.emilla.command.core;

import static android.content.Intent.ACTION_SENDTO;
import static android.content.Intent.ACTION_SEND_MULTIPLE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.annotation.Nullable;

import net.emilla.R;
import net.emilla.action.FileFetcher;
import net.emilla.action.MediaFetcher;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.contact.fragment.EmailFragment;
import net.emilla.content.receive.EmailReceiver;
import net.emilla.util.Apps;
import net.emilla.util.Patterns;

import java.util.ArrayList;

final class Email extends CoreDataCommand implements EmailReceiver {
    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, new Intent(ACTION_SENDTO, Uri.parse("mailto:")));
    }

    private final EmailFragment mEmailFragment = EmailFragment.newInstance();

    @internal Email(AssistActivity act) {
        super(act, CoreEntry.EMAIL, R.string.data_hint_email);

        String entry = CoreEntry.EMAIL.name();
        giveGadgets(
            mEmailFragment,
            new FileFetcher(act, entry, "*/*"),
            // Todo: Thunderbird doesn't like certain filetypes. Can we find a
            //  type statement that's consistently email-friendly?
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
        String addresses = mEmailFragment.selectedContacts();
        if (addresses != null) {
            recipients = addresses;
        }
        email(act, recipients, body);
        // Todo: validate the raw recipients
    }

    private void email(AssistActivity act, String addresses, @Nullable String body) {
        ArrayList<Uri> attachments = act.attachments(CoreEntry.EMAIL.name());
        Intent email;
        var sendTo = new Intent(ACTION_SENDTO, Uri.parse("mailto:"));
        if (attachments == null) {
            email = sendTo;
        } else {
            email = new Intent(ACTION_SEND_MULTIPLE).putExtra(Intent.EXTRA_STREAM, attachments);
            email.setSelector(sendTo);
        }
        email.putExtra(Intent.EXTRA_EMAIL, Patterns.TRIMMING_CSV.split(addresses));
        // Todo: CC and BCC selections
        if (body != null) {
            email.putExtra(Intent.EXTRA_TEXT, body);
        }
        String subject = mEmailFragment.subject();
        if (subject != null) {
            email.putExtra(Intent.EXTRA_SUBJECT, subject);
        }
        giveApp(act, email);
    }

    @Override
    public void provide(AssistActivity act, String emailAddress) {
        email(act, emailAddress, act.dataText());
    }
}
