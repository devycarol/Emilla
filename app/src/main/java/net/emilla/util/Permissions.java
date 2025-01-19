package net.emilla.util;

import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.WRITE_CONTACTS;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.app.Activity;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.permission.PermissionReceiver;
import net.emilla.run.PermissionFailure;
import net.emilla.run.PermissionOffering;

public final class Permissions {

    /**
     * <p>
     * Queries if phone permission has been granted and initiates permission request flow if it
     * hasn't.</p>
     * <p>
     * If the system permission request is suppressed, a fail dialog will link the user to the app
     * info screen where they can manually grant permission.</p>
     *
     * @param act is used to perform permission checks and construct dialogs as needed.
     * @param receiver handler object for permission retrieval.
     * @return true if calling permission is granted, false if it's not.
     */
    public static boolean phone(AssistActivity act, PermissionReceiver receiver) {
        return permissionFlow(act, CALL_PHONE, receiver, R.string.perm_calling);
    }

    /**
     * <p>
     * Queries if contacts permission has been granted and initiates permission request flow if it
     * hasn't.</p>
     * <p>
     * If the system permission request is suppressed, a fail dialog will link the user to the app
     * info screen where they can manually grant permission.</p>
     *
     * @param act is used to perform permission checks and construct dialogs as needed.
     * @param receiver handler object for permission retrieval.
     * @return true if read/write contacts permission is granted, false if it's not.
     */
    public static boolean contacts(AssistActivity act, @Nullable PermissionReceiver receiver) {
        String[] readWriteContacts = {READ_CONTACTS, WRITE_CONTACTS};
        return permissionFlow(act, readWriteContacts, receiver, R.string.perm_contacts);
    }

    private static boolean permissionFlow(AssistActivity act, String permission,
            @Nullable PermissionReceiver receiver, @StringRes int permissionName) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;

        if (act.checkSelfPermission(permission) == PERMISSION_GRANTED) return true;

        if (act.shouldShowRequestPermissionRationale(permission)) {
            act.offer(new PermissionOffering(act, permission, receiver));
        } else act.fail(new PermissionFailure(act, permissionName));

        return false;
    }

    private static boolean permissionFlow(AssistActivity act, String[] permissions,
            @Nullable PermissionReceiver receiver, @StringRes int permissionName) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;

        for (String perm : permissions) if (act.checkSelfPermission(perm) != PERMISSION_GRANTED) {
            if (canShowPrompt(act, permissions)) {
                act.offer(new PermissionOffering(act, permissions, receiver));
            } else act.fail(new PermissionFailure(act, permissionName));

            return false;
        }

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static boolean canShowPrompt(Activity act, String[] permissions) {
        for (String perm : permissions) {
            if (!act.shouldShowRequestPermissionRationale(perm)) return false;
        }
        return true;
    }

    private Permissions() {}
}
