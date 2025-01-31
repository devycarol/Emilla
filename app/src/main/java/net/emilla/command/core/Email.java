package net.emilla.command.core;

import static android.content.Intent.*;

import android.content.Intent;
import android.net.Uri;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.action.FileFetcher;
import net.emilla.action.MediaFetcher;
import net.emilla.action.field.FieldToggle;
import net.emilla.action.field.SubjectField;
import net.emilla.contact.fragment.ContactEmailsFragment;
import net.emilla.content.receive.EmailReceiver;
import net.emilla.settings.Aliases;

public class Email extends AttachCommand implements EmailReceiver {

    public static final String ENTRY = "email";
    @StringRes
    public static final int NAME = R.string.command_email;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_email;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static Yielder yielder() {
        return new Yielder(true, Email::new, ENTRY, NAME, ALIASES);
    }

    private static class EmailParams extends CoreDataParams {

        private EmailParams() {
            super(NAME,
                  R.string.instruction_email,
                  R.drawable.ic_email,
                  R.string.summary_email,
                  R.string.manual_email,
                  R.string.data_hint_email);
        }
    }

    private FieldToggle mSubjectToggle;
    private FileFetcher mFileFetcher;
    private MediaFetcher mMediaFetcher;
    private ContactEmailsFragment mContactsFragment;

    public Email(AssistActivity act) {
        super(act, new EmailParams());
    }

    @Override
    protected void onInit() {
        super.onInit();

        if (mContactsFragment == null) mContactsFragment = ContactEmailsFragment.newInstance(true);
        activity.giveActionBox(mContactsFragment);

        if (mSubjectToggle == null) mSubjectToggle = new SubjectField(activity);
        else if (mSubjectToggle.activated()) reshowField(SubjectField.FIELD_ID);
        giveAction(mSubjectToggle);

        if (mFileFetcher == null) mFileFetcher = new FileFetcher(activity, this, "*/*");
        // TODO: Thunderbird doesn't like certain filetypes. See if you can find a type statement
        //  that's consistently email-friendly.
        giveAction(mFileFetcher);
        if (mMediaFetcher == null) mMediaFetcher = new MediaFetcher(activity, this);
        giveAction(mMediaFetcher);
    }

    @Override
    protected void onInstruct(String instruction) {
        super.onInstruct(instruction);
        mContactsFragment.search(instruction);
    }

    @Override
    protected void onClean() {
        super.onClean();

        activity.removeActionBox(mContactsFragment);
        mContactsFragment = null;

        removeAction(FileFetcher.ID);
        removeAction(MediaFetcher.ID);
    }

    @Override
    protected void run() {
        tryEmail("", null);
    }

    @Override
    protected void run(@NonNull String recipients) {
        tryEmail(recipients, null);
    }

    @Override
    protected void runWithData(@NonNull String body) {
        tryEmail("", body);
    }

    @Override
    protected void runWithData(@NonNull String recipients, @NonNull String body) {
        tryEmail(recipients, body);
    }

    private void tryEmail(@NonNull String recipients, @Nullable String body) {
        String addresses = mContactsFragment.selectedContacts();
        if (addresses != null) email(addresses, body);
        else email(recipients, body);
        // todo: validate the raw recipients
    }

    private void email(@NonNull String addresses, @Nullable String body) {
        Intent email;
        Intent sendTo = new Intent(ACTION_SENDTO, Uri.parse("mailto:"));
        if (attachments == null) email = sendTo;
        else {
            email = new Intent(ACTION_SEND_MULTIPLE).putExtra(EXTRA_STREAM, attachments);
            email.setSelector(sendTo);
        }
        email.putExtra(EXTRA_EMAIL, addresses.split(", *"));
        // TODO DONTMERGE: CC and BCC fields

        if (body != null) email.putExtra(EXTRA_TEXT, body);

        String subject = mSubjectToggle.fieldText();
        if (subject != null) email.putExtra(EXTRA_SUBJECT, subject);

        appSucceed(email);
    }

    @Override
    public void provide(@NonNull String emailAddress) {
        email(emailAddress, activity.dataText());
    }
}
