package net.emilla.commands;

import static android.content.Intent.ACTION_SENDTO;
import static android.content.Intent.EXTRA_SUBJECT;
import static android.content.pm.PackageManager.FEATURE_TELEPHONY_MESSAGING;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.annotation.ArrayRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exceptions.EmlaAppsException;
import net.emilla.exceptions.EmlaFeatureException;
import net.emilla.utils.Apps;
import net.emilla.utils.Contact;

import java.util.HashMap;

public class CommandSms extends CoreDataCommand {
private final Intent mIntent = Apps.newTask(ACTION_SENDTO, Uri.parse("sms:"));
private final HashMap<String, String> mPhoneMap;
private boolean mShowSubjectField;

@Override
public CharSequence lcName() { // The initialism "SMS" shouldn't be lowercased.
    return resources().getString(R.string.command_sms);
}

@Override @ArrayRes
public int detailsId() {
    return R.array.details_sms;
}

@Override @StringRes
public int dataHint() {
    return R.string.data_hint_sms;
}

@Override @DrawableRes
public int icon() {
    return R.drawable.ic_sms;
}

public CommandSms(final AssistActivity act, final String instruct) {
    super(act, instruct, R.string.command_sms, R.string.instruction_phone);
    mPhoneMap = Contact.mapPhones(act.prefs());
}

@Override
public void init() {
    super.init();

    if (mShowSubjectField) toggleField(R.id.field_subject, R.string.field_subject, false);
    giveFieldToggle(R.id.action_field_subject, R.string.field_subject, R.drawable.ic_subject,
            v -> mShowSubjectField = toggleField(R.id.field_subject, R.string.field_subject, true));
    // Todo: attachments
}

@Override
public void clean() {
    super.clean();

    removeAction(R.id.action_field_subject);
    hideField(R.id.field_subject);
}

private void launchMessenger(final Intent intent) {
    final PackageManager pm = packageManager();
    if (!pm.hasSystemFeature(FEATURE_TELEPHONY_MESSAGING)) throw new EmlaFeatureException("Your device doesn't support SMS messaging."); // TODO: handle at install—don't store in sharedprefs in case of settings sync/transfer
    if (pm.resolveActivity(intent, 0) == null) throw new EmlaAppsException("No SMS app found on your device."); // todo handle at mapping
    final String subject = fieldText(R.id.field_subject);
    // TODO: I've not gotten this to work yet
    succeed(subject == null ? intent : intent.putExtra(EXTRA_SUBJECT, subject));
}

@NonNull
private Intent withMsg(final Intent intent, final String message) {
    return intent.putExtra("sms_body", message);
    // overwrites any existing draft to the recipient TODO: detect, warn, confirm.
}

@NonNull
private Intent withRecipients(final Intent intent, final String recipients) {
    return intent.setData(Uri.parse("sms:" + Contact.namesToPhones(recipients, mPhoneMap)));
}

@Override
protected void run() {
    launchMessenger(mIntent);
}

@Override
protected void run(final String recipients) {
    launchMessenger(withRecipients(mIntent, recipients));
}

@Override
protected void runWithData(final String message) {
    launchMessenger(withMsg(mIntent, message));
}

@Override
protected void runWithData(final String recipients, final String message) {
    launchMessenger(withMsg(withRecipients(mIntent, recipients), message));
}
}
