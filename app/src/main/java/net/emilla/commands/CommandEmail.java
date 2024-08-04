package net.emilla.commands;

import static android.content.Intent.*;
import static java.util.regex.Pattern.compile;

import android.content.Intent;
import android.net.Uri;

import androidx.annotation.ArrayRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exceptions.EmlaAppsException;
import net.emilla.utils.Apps;
import net.emilla.utils.Contacts;
import net.emilla.utils.Tags;

import java.util.HashMap;
import java.util.regex.Matcher;

public class CommandEmail extends CoreDataCommand {
private static final String FLAG_ATTACH = " */a(t(ta)?ch)?"; // TODO: lang

private final Intent mIntent = Apps.newTask(ACTION_SENDTO, Uri.parse("mailto:"));
private final Intent mSendMultipleIntent = Apps.newTask(ACTION_SEND_MULTIPLE); // todo: should this have "mailto:"?
private final HashMap<String, String> mEmailMap;

@Override
public Command cmd() {
    return Command.EMAIL;
}

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

public CommandEmail(final AssistActivity act) {
    super(act, R.string.command_email, R.string.instruction_email);
    mEmailMap = act.emailMap();
}

private void clearDetails() {
    mIntent.removeExtra(EXTRA_CC);
    mIntent.removeExtra(EXTRA_BCC);
    mIntent.removeExtra(EXTRA_EMAIL);
    mIntent.removeExtra(EXTRA_SUBJECT);
}

private void putRecipients(final Intent intent, final String recipients) {
    final String[] peopleAndSubject = recipients.split(" *\\| *", 2);
    String people = peopleAndSubject[0];
    // todo: validate the emails
    people = Tags.putEmailsIfPresent(people, Tags.CC, intent, EXTRA_CC, Tags.EMAIL_TAGS, mEmailMap);
    people = Tags.putEmailsIfPresent(people, Tags.BCC, intent, EXTRA_BCC, Tags.EMAIL_TAGS, mEmailMap);
    if (Tags.itHas(people, Tags.tTO)) {
        final String to = Tags.getFrom(people, Tags.tTO, Tags.EMAIL_TAGS);
        people = Tags.strip(people, Tags.tTO, to);
        if (people.isEmpty()) people = to;
        else people = people + ',' + to;
        intent.putExtra(EXTRA_EMAIL, Contacts.namesToEmails(people, mEmailMap));
    } else if (!people.isEmpty()) intent.putExtra(EXTRA_EMAIL, Contacts.namesToEmails(people, mEmailMap));
    if (peopleAndSubject.length > 1) {
        final String subject = peopleAndSubject[1];
        if (!subject.isEmpty()) intent.putExtra(EXTRA_SUBJECT, subject);
    }
}

private Intent putAttachmentsAndRecipients(final String recipients) {
    final Matcher m = compile(FLAG_ATTACH).matcher(recipients);
    if (m.find()) {
        final AssistActivity act = activity();
        if (act.mAttachments == null) {
            act.getFiles();
            return null;
        }
        mSendMultipleIntent.putExtra(EXTRA_STREAM, act.mAttachments);
        mSendMultipleIntent.setSelector(mIntent);
        act.mAttachments = null; // this will overwrite attachments if /pic is added
        final String actualRecipients = m.replaceFirst("");
        if (!actualRecipients.isEmpty()) putRecipients(mSendMultipleIntent, actualRecipients);
        return mSendMultipleIntent;
    }
    putRecipients(mIntent, recipients);
    return mIntent;
}

@Override
public void run() {
    if (mIntent.resolveActivity(packageManager()) == null) {
        clearDetails();
        throw new EmlaAppsException("No email app found on your device."); // todo handle at mapping
    }
    succeed(mIntent);
}

@Override
public void run(final String recipients) {
    final Intent in = putAttachmentsAndRecipients(recipients);
    if (in != null) {
        if (in.resolveActivity(packageManager()) == null) {
            clearDetails();
            throw new EmlaAppsException("No email app found on your device."); // todo handle at mapping
        }
        succeed(in);
    }
}

@Override
public void runWithData(final String body) {
    if (mIntent.resolveActivity(packageManager()) == null) {
        clearDetails();
        throw new EmlaAppsException("No email app found on your device."); // todo handle at mapping
    }
    succeed(mIntent.putExtra(EXTRA_TEXT, body));
}

@Override
public void runWithData(final String recipients, final String body) {
    final Intent in = putAttachmentsAndRecipients(recipients);
    if (in != null) {
        if (in.resolveActivity(packageManager()) == null) { // todo handle at mapping
            clearDetails();
            throw new EmlaAppsException("No email app found on your device.");
        }
        succeed(in.putExtra(EXTRA_TEXT, body));
    }
}
}
