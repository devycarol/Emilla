package net.emilla.run;

import android.app.Activity;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/// Presents the user with a system permission request.
///
/// This can only be used when [Activity#shouldShowRequestPermissionRationale(String)] is true for
/// the permission(s) being requested.
@RequiresApi(Build.VERSION_CODES.M)
public enum PermissionOffering {
    ;
    public static CommandRun instance(String[] permissions, @Nullable Runnable onGrant) {
        return act -> act.offerPermissions(permissions, onGrant);
    }
}
