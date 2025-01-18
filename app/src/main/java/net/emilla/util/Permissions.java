package net.emilla.util;

import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.READ_CONTACTS;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.run.DialogFailure;
import net.emilla.run.DialogOffering;
import net.emilla.run.PermissionOffering;

public final class Permissions {

    private static final int
            REQUEST_PHONE = 1,
            REQUEST_CONTACTS = 2;

    private static AlertDialog.Builder courtesyDialog(AssistActivity act, String permissionId,
            @StringRes int permissionName, @StringRes int consentMessage) {
        return Dialogs.yesNo(act, permissionName, consentMessage, (dlg, which) -> {
            act.prefs().edit().putBoolean(permissionId, true).apply();
            dlg.cancel(); // Todo: don't require this
        });
        // Should there be a settings page to revoke these? It's just courtesy, but it could be
        // respectful to have a setting that withdraws all of these
    }

    private static AlertDialog.Builder permissionDialog(AssistActivity act, PackageManager pm,
            @StringRes int permissionName) {
        Intent appInfo = Apps.infoTask();
        if (appInfo.resolveActivity(pm) != null) return Dialogs.dual(act, permissionName,
                R.string.dlg_msg_perm_denial, R.string.app_info, (dlg, which) -> {
            act.startActivity(appInfo);
            act.suppressResumeChime();
            dlg.cancel(); // Todo: don't require this
        });

        return Dialogs.base(act, permissionName, R.string.dlg_msg_perm_denial, android.R.string.ok);
        // this should pretty much never happen.
    }

    private static boolean permission(AssistActivity act, PackageManager pm, String permissionId,
            int requestCode, @StringRes int permissionName, @StringRes int consentMessage) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (act.checkSelfPermission(permissionId) == PERMISSION_GRANTED) return true;
            boolean prompt = act.shouldShowRequestPermissionRationale(permissionId);
            if (prompt) act.offer(new PermissionOffering(act, permissionId, requestCode));
            else act.fail(new DialogFailure(act, permissionDialog(act, pm, permissionName)));
            return false;
        }
        if (act.prefs().getBoolean(permissionId, false)) return true;
        // A courtesy dialog is offered for pre-Marshmallow users, even if permissions are granted at
        // install.
        act.offer(new DialogOffering(act, courtesyDialog(act, permissionId, permissionName,
                consentMessage)));
        return false;
    }

    public static boolean phone(AssistActivity act, PackageManager pm) {
        return permission(act, pm, CALL_PHONE, REQUEST_PHONE, R.string.perm_calling,
                R.string.perm_consent_call);
    }

    public static boolean readContacts(AssistActivity act, PackageManager pm) {
        return permission(act, pm, READ_CONTACTS, REQUEST_CONTACTS, R.string.perm_contacts,
                R.string.perm_consent_contacts);
    }

    private Permissions() {}
}
