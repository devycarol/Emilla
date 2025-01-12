package net.emilla.command.core;

import static android.content.Intent.*;

import android.content.Intent;
import android.net.Uri;

import androidx.annotation.ArrayRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.action.FileFetcher;
import net.emilla.action.MediaFetcher;
import net.emilla.settings.Aliases;
import net.emilla.utils.Contacts;
import net.emilla.utils.EmailTags;

import java.util.HashMap;

public class Email extends AttachCommand {

    public static final String ENTRY = "email";
    @ArrayRes
    public static final int ALIASES = R.array.aliases_email;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    private static class EmailParams extends CoreDataParams {

        private EmailParams() {
            super(R.string.command_email,
                  R.string.instruction_email,
                  R.drawable.ic_email,
                  R.string.summary_email,
                  R.string.manual_email,
                  R.string.data_hint_email);
        }
    }

    private HashMap<String, String> mEmailMap;
    private FileFetcher mFileFetcher;
    private MediaFetcher mMediaFetcher;

    public Email(AssistActivity act, String instruct) {
        super(act, instruct, new EmailParams());
    }

    @Override
    public void init(boolean updateTitle) {
        super.init(updateTitle);

        if (mFileFetcher == null) mFileFetcher = new FileFetcher(activity, this, "*/*");
        // TODO: Thunderbird doesn't like certain filetypes. See if you can find a type statement
        //  that's consistently email-friendly.
        giveAction(mFileFetcher);
        if (mMediaFetcher == null) mMediaFetcher = new MediaFetcher(activity, this);
        giveAction(mMediaFetcher);

        mEmailMap = Contacts.mapEmails(activity.prefs());
    }

    @Override
    public void clean() {
        super.clean();

        removeAction(FileFetcher.ID);
        removeAction(MediaFetcher.ID);
    }

    private Intent makeIntent() {
        Intent sendTo = new Intent(ACTION_SENDTO, Uri.parse("mailto:"));
        if (attachments == null) return sendTo;
        Intent sendMultiple = new Intent(ACTION_SEND_MULTIPLE).putExtra(EXTRA_STREAM, attachments);
        // TODO: should this have "mailto:"?
        sendMultiple.setSelector(sendTo);
        return sendMultiple;
    }

    private Intent makeIntent(String recipients) {
        Intent intent = makeIntent();
        String[] peopleAndSubject = recipients.split(" *\\| *", 2);
        String people = peopleAndSubject[0];
        // todo: validate the emails
        people = EmailTags.putEmailsIfPresent(people, EmailTags.CC, intent, EXTRA_CC, EmailTags.EMAIL_TAGS, mEmailMap);
        people = EmailTags.putEmailsIfPresent(people, EmailTags.BCC, intent, EXTRA_BCC, EmailTags.EMAIL_TAGS, mEmailMap);
        if (EmailTags.itHas(people, EmailTags.TO)) {
            String to = EmailTags.getFrom(people, EmailTags.TO, EmailTags.EMAIL_TAGS);
            people = EmailTags.strip(people, EmailTags.TO, to);
            if (people.isEmpty()) people = to;
            else people = people + ',' + to;
            intent.putExtra(EXTRA_EMAIL, Contacts.namesToEmails(people, mEmailMap));
        } else if (!people.isEmpty()) intent.putExtra(EXTRA_EMAIL, Contacts.namesToEmails(people, mEmailMap));
        if (peopleAndSubject.length > 1) {
            String subject = peopleAndSubject[1];
            if (!subject.isEmpty()) intent.putExtra(EXTRA_SUBJECT, subject);
        }
        return intent;
    }

    @Override
    protected void run() {
        appSucceed(makeIntent());
    }

    @Override
    protected void run(String recipients) {
        appSucceed(makeIntent(recipients));
    }

    @Override
    protected void runWithData(String body) {
        appSucceed(makeIntent().putExtra(EXTRA_TEXT, body));
    }

    @Override
    protected void runWithData(String recipients, String body) {
        appSucceed(makeIntent(recipients).putExtra(EXTRA_TEXT, body));
    }
}
