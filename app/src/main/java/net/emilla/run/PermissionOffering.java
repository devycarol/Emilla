package net.emilla.run;

import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import net.emilla.activity.AssistActivity;

/**
 * <p>
 * Presents the user with a system permission request.</p>
 * <p>
 * This can only be used when
 * {@link android.app.Activity#shouldShowRequestPermissionRationale(String)} is true for the
 * permission(s) being requested.</p>
 */
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
