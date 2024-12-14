package net.emilla.command.core;

import static android.content.Intent.ACTION_CALL;
import static android.content.pm.PackageManager.FEATURE_TELEPHONY;
import static android.net.Uri.parse;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.DrawableRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exceptions.EmlaAppsException;
import net.emilla.exceptions.EmlaBadCommandException;
import net.emilla.exceptions.EmlaFeatureException;
import net.emilla.utils.Apps;
import net.emilla.utils.Contacts;
import net.emilla.utils.Permissions;

import java.util.HashMap;

public class Call extends CoreCommand {
private final Intent mIntent = Apps.newTask(ACTION_CALL);
private final HashMap<String, String> mPhoneMap;

@Override @ArrayRes
public int detailsId() {
    return R.array.details_call_phone;
}

@Override @DrawableRes
public int icon() {
    return R.drawable.ic_call;
}

@Override
public int imeAction() {
    return EditorInfo.IME_ACTION_GO;
}

public Call(AssistActivity act, String instruct) {
    super(act, instruct, R.string.command_call, R.string.instruction_phone);
    mPhoneMap = Contacts.mapPhones(act.prefs());
}

@Override
protected void run() {
    throw new EmlaBadCommandException("Sorry! I don't have contact selection yet."); // TODO
}

@Override
protected void run(String nameOrNumber) {
    // todo: conference calls?
    // todo: immediate calls to phonewords
    PackageManager pm = packageManager();
    if (!pm.hasSystemFeature(FEATURE_TELEPHONY)) throw new EmlaFeatureException("Your device doesn't support phone calls.");
    if (!Permissions.phone(activity(), pm)) return;
    mIntent.setData(parse("tel:" + Contacts.fromName(nameOrNumber, mPhoneMap)));
    // TODO: handle at mapping/install - make sure it's not 'sticky' in sharedprefs in case of data transfer. it shouldn't disable the "command enabled" pref,
    //  it should just be its own element of an "is the command enabled" check similar to HeliBoard's handling in its "SettingsValues" class.
    if (pm.resolveActivity(mIntent, 0) == null) throw new EmlaAppsException("No phone app found on your device.");
    // todo: handle at mapping (if even necessary. figure out how you want to handle the interesting behavior when telephony app isn't installed.)
    succeed(mIntent); // todo: success chime is cut off by phone call initiation
}
}
