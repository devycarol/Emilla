package net.emilla.commands;

import static android.content.Intent.ACTION_SENDTO;
import static android.content.Intent.EXTRA_SUBJECT;
import static android.content.pm.PackageManager.FEATURE_TELEPHONY_MESSAGING;
import static net.emilla.utils.Tags.SMS_TAGS;
import static java.lang.Character.isWhitespace;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.annotation.ArrayRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exceptions.EmlaAppsException;
import net.emilla.exceptions.EmlaFeatureException;
import net.emilla.utils.Apps;
import net.emilla.utils.Contacts;
import net.emilla.utils.Lang;
import net.emilla.utils.Tags;

import java.util.HashMap;

public class CommandSms extends CoreDataCommand {
private final Intent mIntent = Apps.newTask(ACTION_SENDTO, Uri.parse("sms:"));
private final HashMap<String, String> mPhoneMap;

@Override
public Command cmd() {
    return Command.SMS;
}

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

public CommandSms(final AssistActivity act) {
    super(act, R.string.command_sms, R.string.instruction_phone);
    mPhoneMap = act.phoneMap();
}

private Intent putText(/*mutable*/ String message) {
    if (Tags.itHas(message, Tags.SUBJECT)) {
        final String subject = Tags.getFrom(message, Tags.SUBJECT, SMS_TAGS);
        mIntent.putExtra(EXTRA_SUBJECT, subject); // TODO: I've not gotten this to work yet
        message = Tags.strip(message, Tags.SUBJECT, subject);
        if (message.isEmpty()) return mIntent;
    }
    if (Tags.itHas(message, Tags.BODY)) {
        final String body = Tags.getFrom(message, Tags.BODY, SMS_TAGS);
        message = Tags.strip(message, Tags.BODY, body);
        if (message.isEmpty()) {
            if (body.isEmpty()) return mIntent;
            message = body;
        }
        else if (isWhitespace(message.charAt(message.length() - 1))) message += body;
        else message = Lang.wordConcat(resources(), message, body);
    }
    // will overwrite any existing draft to the recipient - TODO: detect, warn, confirm.
    return mIntent.putExtra("sms_body", message);
}

@Override
public void run() {
    // todo: immediate texting
    final PackageManager pm = packageManager();
    if (!pm.hasSystemFeature(FEATURE_TELEPHONY_MESSAGING)) throw new EmlaFeatureException("Your device doesn't support SMS messaging."); // TODO: handle at install—don't store in sharedprefs in case of settings sync/transfer
    if (pm.resolveActivity(mIntent, 0) == null) throw new EmlaAppsException("No SMS app found on your device."); // todo handle at mapping
    succeed(mIntent);
}

@Override
public void run(final String recipients) {
    // todo: attachments
    final PackageManager pm = packageManager();
    if (!pm.hasSystemFeature(FEATURE_TELEPHONY_MESSAGING)) throw new EmlaFeatureException("Your device doesn't support SMS messaging."); // TODO: handle at install—don't store in sharedprefs in case of settings sync/transfer
    if (pm.resolveActivity(mIntent, 0) == null) throw new EmlaAppsException("No SMS app found on your device."); // todo handle at mapping
    succeed(mIntent.setData(Uri.parse("sms:" + Contacts.namesToPhones(recipients, mPhoneMap))));
}

@Override
public void runWithData(final String message) {
    final PackageManager pm = packageManager();
    if (!pm.hasSystemFeature(FEATURE_TELEPHONY_MESSAGING)) throw new EmlaFeatureException("Your device doesn't support SMS messaging."); // TODO: handle at install—don't store in sharedprefs in case of settings sync/transfer
    if (pm.resolveActivity(mIntent, 0) == null) throw new EmlaAppsException("No SMS app found on your device."); // todo handle at mapping
    succeed(putText(message));
}

@Override
public void runWithData(final String recipients, final String message) {
    final PackageManager pm = packageManager();
    if (!pm.hasSystemFeature(FEATURE_TELEPHONY_MESSAGING)) throw new EmlaFeatureException("Your device doesn't support SMS messaging."); // TODO: handle at install—don't store in sharedprefs in case of settings sync/transfer
    if (pm.resolveActivity(mIntent, 0) == null) throw new EmlaAppsException("No SMS app found on your device."); // todo handle at mapping
    mIntent.setData(Uri.parse("sms:" + Contacts.namesToPhones(recipients, mPhoneMap)));
    succeed(putText(message));
}
}
