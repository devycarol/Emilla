package net.emilla.command.core;

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
import net.emilla.utils.Contacts;

import java.util.HashMap;

public class Sms extends CoreDataCommand {
private final Intent mIntent = Apps.newTask(ACTION_SENDTO, Uri.parse("sms:"));
private final HashMap<String, String> mPhoneMap;
private boolean mShowSubjectField;

@Override
public CharSequence lcName() { // The initialism "SMS" shouldn't be lowercased.
    return string(R.string.command_sms);
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

public Sms(AssistActivity act, String instruct) {
    super(act, instruct, R.string.command_sms, R.string.instruction_phone);
    mPhoneMap = Contacts.mapPhones(act.prefs());
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

private void launchMessenger(Intent intent) {
    PackageManager pm = packageManager();
    if (!pm.hasSystemFeature(FEATURE_TELEPHONY_MESSAGING)) throw new EmlaFeatureException("Your device doesn't support SMS messaging."); // TODO: handle at install—don't store in sharedprefs in case of settings sync/transfer
    if (pm.resolveActivity(intent, 0) == null) throw new EmlaAppsException("No SMS app found on your device."); // todo handle at mapping
    String subject = fieldText(R.id.field_subject);
    // TODO: I've not gotten this to work yet
    succeed(subject == null ? intent : intent.putExtra(EXTRA_SUBJECT, subject));
}

@NonNull
private Intent withMsg(Intent intent, String message) {
    return intent.putExtra("sms_body", message);
    // overwrites any existing draft to the recipient TODO: detect, warn, confirm.
}

@NonNull
private Intent withRecipients(Intent intent, String recipients) {
    return intent.setData(Uri.parse("sms:" + Contacts.namesToPhones(recipients, mPhoneMap)));
}

@Override
protected void run() {
    launchMessenger(mIntent);
}

@Override
protected void run(String recipients) {
    launchMessenger(withRecipients(mIntent, recipients));
}

@Override
protected void runWithData(String message) {
    launchMessenger(withMsg(mIntent, message));
}

@Override
protected void runWithData(String recipients, String message) {
    launchMessenger(withMsg(withRecipients(mIntent, recipients), message));
}
}
