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
public final class PermissionOffering implements Runnable {

    private final AssistActivity mActivity;
    private final String[] mPermissions;
    @Nullable
    private final Runnable mOnGrant;

    public PermissionOffering(AssistActivity act, String permission, @Nullable Runnable onGrant) {
        this(act, new String[]{permission}, onGrant);
    }

    public PermissionOffering(AssistActivity act, String[] permissions, @Nullable Runnable onGrant) {
        mActivity = act;
        mPermissions = permissions;
        mOnGrant = onGrant;
    }

    @Override
    public void run() {
        mActivity.offerPermissions(mPermissions, mOnGrant);
    }
}
