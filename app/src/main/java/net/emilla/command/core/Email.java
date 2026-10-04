package net.emilla.command.core;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import net.emilla.Feedback;
import net.emilla.R;
import net.emilla.action.FileFetcher;
import net.emilla.action.Gadget;
import net.emilla.action.MediaFetcher;
import net.emilla.action.Widget;
import net.emilla.annotation.internal;
import net.emilla.command.EmillaCommand;
import net.emilla.contact.fragment.EmailFragment;
import net.emilla.datafield.DataDirective;
import net.emilla.datafield.DataField;
import net.emilla.util.Apps;
import net.emilla.util.Patterns;
import net.emilla.wadget.ActionSurface;

import java.util.ArrayList;

final class Email extends EmillaCommand {
    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:")));
    }

    private final EmailFragment mEmailFragment = EmailFragment.newInstance();
    private final FileFetcher mFileFetcher;
    private final MediaFetcher mMediaFetcher;

    @internal Email(ActionSurface surface) {
        super(surface, CoreEntry.EMAIL);
        var act = surface.getAssistActivity();
        String entry = CoreEntry.EMAIL.name();
        mFileFetcher = new FileFetcher(act, entry, "*/*");
        // Todo: Thunderbird doesn't like certain filetypes. Can we find a type
        //  statement that's consistently email-friendly?
        mMediaFetcher = new MediaFetcher(act, entry);
    }

    @Override
    public DataDirective dataDirective() {
        return new DataField(R.string.data_hint_email);
    }

    @Override
    protected Widget[] widgets() {
        return new Widget[] {
            mEmailFragment,
            mFileFetcher,
            mMediaFetcher,
        };
    }

    @Override
    protected Gadget[] gadgets() {
        return new Gadget[] {
            mEmailFragment,
        };
    }

    @Override
    protected Feedback run(ActionSurface surface) {
        return run(surface, "");
    }

    @Override
    protected Feedback run(ActionSurface surface, String recipients) {
        String addresses = mEmailFragment.selectedContacts();
        if (addresses != null) {
            recipients = addresses;
        }
        // Todo: validate the raw recipients
        var act = surface.getAssistActivity();
        ArrayList<Uri> attachments = act.attachments(CoreEntry.EMAIL.name());
        Intent email;
        var sendTo = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
        if (attachments == null) {
            email = sendTo;
        } else {
            email = new Intent(Intent.ACTION_SEND_MULTIPLE)
                .putExtra(Intent.EXTRA_STREAM, attachments)
            ;
            email.setSelector(sendTo);
        }
        email.putExtra(
            Intent.EXTRA_EMAIL,
            Patterns.TRIMMING_CSV.split(recipients)
        );
        // Todo: CC and BCC selections
        String body = surface.dataText();
        if (body != null) {
            email.putExtra(Intent.EXTRA_TEXT, body);
        }
        String subject = mEmailFragment.subject();
        if (subject != null) {
            email.putExtra(Intent.EXTRA_SUBJECT, subject);
        }
        return Feedback.give(email);
    }
}
