package net.emilla.commands;

import static android.content.Intent.ACTION_CALL;
import static android.content.pm.PackageManager.FEATURE_TELEPHONY;
import static android.net.Uri.parse;

import android.content.Intent;
import android.content.pm.PackageManager;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exceptions.EmlaAppsException;
import net.emilla.exceptions.EmlaBadCommandException;
import net.emilla.exceptions.EmlaFeatureException;
import net.emilla.utils.Apps;
import net.emilla.utils.Contacts;
import net.emilla.utils.Permissions;

import java.util.HashMap;

public class CommandCall extends CoreCommand {
private final Intent mIntent = Apps.newTask(ACTION_CALL);
private final HashMap<String, String> mPhoneMap;

public CommandCall(final AssistActivity act) {
    super(act, R.string.command_call, R.string.instruction_phone);
    mPhoneMap = act.phoneMap();
}

@Override
public Command cmd() {
    return Command.CALL;
}

@Override
public void run() {
    throw new EmlaBadCommandException("Sorry! I don't have contact selection yet."); // TODO
}

@Override
public void run(final String nameOrNumber) {
    // todo: conference calls?
    // todo: immediate calls to phonewords
    final PackageManager pm = packageManager();
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
