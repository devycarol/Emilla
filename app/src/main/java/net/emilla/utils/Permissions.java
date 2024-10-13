package net.emilla.utils;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;

public class Permissions {
private static final String
    PERM_PHONE = Manifest.permission.CALL_PHONE;
public static final int
    REQUEST_PHONE = 1;

private static AlertDialog courtesyDialog(AssistActivity act, String permission,
        @StringRes int permDescId) {
    Resources res = act.getResources();
    CharSequence msg = res.getString(R.string.dlg_msg_perm_consent, res.getString(permDescId));
    return Dialogs.yesNoMsg(act, R.string.dialog_permissions, msg, (dialog, which) -> {
        act.prefs().edit().putBoolean(permission, true).apply();
        act.onCloseDialog(true);
    }).create();
    // Should there be a settings page to revoke these? It's just courtesy, but it could be
    // respectful to have a setting that withdraws all of these
}

private static AlertDialog permissionDialog(AssistActivity act, PackageManager pm,
        @StringRes int permId) {
    Intent settings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Apps.pkgUri(act.getPackageName()));
    boolean noAppInfo = settings.resolveActivity(pm) == null;
    if (noAppInfo) settings.setAction(Settings.ACTION_SETTINGS).setData(null)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    Resources res = act.getResources();
    CharSequence msg = res.getString(R.string.dlg_msg_perm_denial, res.getString(permId));
    if (settings.resolveActivity(pm) == null) return Dialogs.msg(act, R.string.dialog_permissions, msg)
            .setNeutralButton(R.string.close, (dialog, which) -> act.onCloseDialog(true)).create();
    int posId = noAppInfo ? R.string.settings : R.string.app_info;
    return Dialogs.okCancelMsg(act, R.string.dialog_permissions, msg, posId, (dialog, which) -> {
        act.startActivity(settings);
        act.onCloseDialog(false);
    }).create();
}

public static boolean phone(AssistActivity act, PackageManager pm) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (act.checkSelfPermission(PERM_PHONE) == PERMISSION_GRANTED) return true;
        boolean hasPrompt = act.shouldShowRequestPermissionRationale(PERM_PHONE);
        if (hasPrompt) act.offer(PERM_PHONE, REQUEST_PHONE);
        else act.fail(permissionDialog(act, pm, R.string.perm_calling));
        return false;
    }
    if (act.prefs().getBoolean(PERM_PHONE, false)) return true;
    // A courtesy dialog is offered for pre-Marshmallow users, even if permissions are granted at
    // install.
    act.offer(courtesyDialog(act, PERM_PHONE, R.string.perm_desc_calling));
    return false;
}

private Permissions() {}
}
