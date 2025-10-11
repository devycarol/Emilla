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

import androidx.annotation.ArrayRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.action.FileFetcher;
import net.emilla.action.MediaFetcher;
import net.emilla.action.field.FieldToggle;
import net.emilla.action.field.SubjectField;
import net.emilla.activity.AssistActivity;
import net.emilla.app.Apps;
import net.emilla.contact.fragment.ContactEmailsFragment;
import net.emilla.content.receive.EmailReceiver;

import java.util.ArrayList;

public final class Email extends CoreDataCommand implements EmailReceiver {

    public static final String ENTRY = "email";
    @StringRes
    public static final int NAME = R.string.command_email;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_email;

    public static Yielder yielder() {
        return new Yielder(true, Email::new, ENTRY, NAME, ALIASES);
    }

    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, new Intent(ACTION_SENDTO, Uri.parse("mailto:")));
    }

    private FieldToggle mSubjectToggle = null;
    private FileFetcher mFileFetcher = null;
    private MediaFetcher mMediaFetcher = null;
    private ContactEmailsFragment mContactsFragment = null;

    private Email(AssistActivity act) {
        super(act, NAME,
              R.string.instruction_email,
              R.drawable.ic_email,
              R.string.summary_email,
              R.string.manual_email,
              R.string.data_hint_email);
    }

    @Override
    protected void onInit() {
        super.onInit();

        if (mContactsFragment == null) {
            mContactsFragment = ContactEmailsFragment.newInstance(true);
        }
        this.activity.giveActionBox(mContactsFragment);

        if (mSubjectToggle == null) {
            mSubjectToggle = new SubjectField(this.activity);
        } else if (mSubjectToggle.activated()) {
            reshowField(SubjectField.FIELD_ID);
        }
        giveAction(mSubjectToggle);

        if (mFileFetcher == null) {
            mFileFetcher = new FileFetcher(this.activity, ENTRY, "*/*");
        }
        giveAction(mFileFetcher);
        // TODO: Thunderbird doesn't like certain filetypes. See if you can find a type statement
        //  that's consistently email-friendly.

        if (mMediaFetcher == null) {
            mMediaFetcher = new MediaFetcher(this.activity, ENTRY);
        }
        giveAction(mMediaFetcher);
    }

    @Override
    protected void onInstruct(@Nullable String instruction) {
        super.onInstruct(instruction);
        mContactsFragment.search(instruction);
    }

    @Override
    protected void onClean() {
        super.onClean();

        this.activity.removeActionBox(mContactsFragment);
        mContactsFragment = null;

        removeAction(FileFetcher.ID);
        removeAction(MediaFetcher.ID);
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
