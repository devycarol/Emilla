package net.emilla.run;

import android.app.Activity;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import net.emilla.activity.AssistActivity;

/// Presents the user with a system permission request.
///
/// This can only be used when [Activity#shouldShowRequestPermissionRationale(String)] is true for
/// the permission(s) being requested.
@RequiresApi(api = Build.VERSION_CODES.M)
public final class PermissionOffering implements CommandRun {

    private final String[] mPermissions;
    @Nullable
    private final Runnable mOnGrant;

    public PermissionOffering(String permission, @Nullable Runnable onGrant) {
        this(new String[]{permission}, onGrant);
    }

    public PermissionOffering(String[] permissions, @Nullable Runnable onGrant) {
        mPermissions = permissions;
        mOnGrant = onGrant;
    }

    @Override
    public void run(AssistActivity act) {
        act.offerPermissions(mPermissions, mOnGrant);
    }
}
