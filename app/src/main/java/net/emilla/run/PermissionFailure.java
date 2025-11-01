package net.emilla.run;

import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.apps.Apps;
import net.emilla.util.Dialogs;

public final class PermissionFailure extends DialogRun {

    private static AlertDialog.Builder dialog(AssistActivity act, @StringRes int permissionName) {
        // todo: this often results in an activity restart, which messes with the resume chime and
        //  probably other elements of state. handle accordingly.
        Intent appInfo = Apps.infoTask();
        PackageManager pm = act.getPackageManager();
        if (appInfo.resolveActivity(pm) != null) {
            return Dialogs.dual(act, permissionName, R.string.dlg_msg_perm_denial, R.string.app_info,
                                (dlg, which) -> act.startActivity(appInfo));
        }

        return Dialogs.message(act, permissionName, R.string.dlg_msg_perm_denial);
        // this should pretty much never happen.
    }

    public PermissionFailure(AssistActivity act, @StringRes int permissionName) {
        super(dialog(act, permissionName));
    }
}
