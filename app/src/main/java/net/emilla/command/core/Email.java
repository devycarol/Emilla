package net.emilla.command.core;

import static android.content.Intent.*;
import static java.util.regex.Pattern.compile;

import android.content.Intent;
import android.net.Uri;

import androidx.annotation.ArrayRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exception.EmlaAppsException;
import net.emilla.utils.Contacts;
import net.emilla.utils.EmailTags;

import java.util.HashMap;
import java.util.regex.Matcher;

public class Email extends CoreDataCommand {

    private static final String FLAG_ATTACH = " */a(t(ta)?ch)?"; // Todo lang

    private final Intent mIntent = new Intent(ACTION_SENDTO, Uri.parse("mailto:"));
    private final Intent mSendMultipleIntent = new Intent(ACTION_SEND_MULTIPLE); // Todo: should this have "mailto:"?
    private final HashMap<String, String> mEmailMap;

    @Override @ArrayRes
    public int detailsId() {
        return R.array.details_email;
    }

    @Override @StringRes
    public int dataHint() {
        return R.string.data_hint_email;
    }

    @Override @DrawableRes
    public int icon() {
        return R.drawable.ic_email;
    }

    public Email(AssistActivity act, String instruct) {
        super(act, instruct, R.string.command_email, R.string.instruction_email);
        mEmailMap = Contacts.mapEmails(act.prefs());
    }

    private void clearDetails() {
        mIntent.removeExtra(EXTRA_CC);
        mIntent.removeExtra(EXTRA_BCC);
        mIntent.removeExtra(EXTRA_EMAIL);
        mIntent.removeExtra(EXTRA_SUBJECT);
    }

    private Intent putRecipients(Intent intent, String recipients) {
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

    private Intent putAttachmentsAndRecipients(String recipients) {
        Matcher m = compile(FLAG_ATTACH).matcher(recipients);
        if (m.find()) {
            AssistActivity act = activity;
            if (act.attachments() == null) {
                act.getFiles();
                return null;
            }
            mSendMultipleIntent.putExtra(EXTRA_STREAM, act.attachments());
            mSendMultipleIntent.setSelector(mIntent);
            act.nullifyAttachments(); // this will overwrite attachments if /pic is added
            String actualRecipients = m.replaceFirst("");
            return actualRecipients.isEmpty() ? mSendMultipleIntent : putRecipients(mSendMultipleIntent,
                    actualRecipients);
        }
        return putRecipients(mIntent, recipients);
    }

    @Override
    protected void run() {
        if (mIntent.resolveActivity(packageManager()) == null) {
            clearDetails();
            throw new EmlaAppsException("No email app found on your device."); // todo handle at mapping
        }
        appSucceed(mIntent);
    }

    @Override
    protected void run(String recipients) {
        Intent in = putAttachmentsAndRecipients(recipients);
        if (in == null) return;
        if (in.resolveActivity(packageManager()) == null) {
            clearDetails();
            throw new EmlaAppsException("No email app found on your device."); // todo handle at mapping
        }
        appSucceed(in);
    }

    @Override
    protected void runWithData(String body) {
        if (mIntent.resolveActivity(packageManager()) == null) {
            clearDetails();
            throw new EmlaAppsException("No email app found on your device."); // todo handle at mapping
        }
        appSucceed(mIntent.putExtra(EXTRA_TEXT, body));
    }

    @Override
    protected void runWithData(String recipients, String body) {
        Intent in = putAttachmentsAndRecipients(recipients);
        if (in == null) return;
        if (in.resolveActivity(packageManager()) == null) { // todo handle at mapping
            clearDetails();
            throw new EmlaAppsException("No email app found on your device.");
        }
        appSucceed(in.putExtra(EXTRA_TEXT, body));
    }
}
