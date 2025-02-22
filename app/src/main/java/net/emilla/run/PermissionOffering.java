package net.emilla.run;

import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import net.emilla.AssistActivity;
import net.emilla.permission.PermissionReceiver;

/**
 * <p>
 * Presents the user with a system permission request.</p>
 * <p>
 * This can only be used when
 * {@link android.app.Activity#shouldShowRequestPermissionRationale(String)} is true for the
 * permission(s) being requested.</p>
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public final class PermissionOffering implements Offering {

    private final AssistActivity mActivity;
    private final String[] mPermissions;
    @Nullable
    private final PermissionReceiver mReceiver;

    public PermissionOffering(
        AssistActivity act,
        String permission,
        @Nullable PermissionReceiver receiver
    ) {
        this(act, new String[]{permission}, receiver);
    }

    public PermissionOffering(
        AssistActivity act,
        String[] permissions,
        @Nullable PermissionReceiver receiver
    ) {
        mActivity = act;
        mPermissions = permissions;
        mReceiver = receiver;
    }

    @Override
    public void run() {
        mActivity.permissionRetriever.retrieve(mPermissions, mReceiver);
    }
}
