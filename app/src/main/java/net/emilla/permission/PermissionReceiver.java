package net.emilla.permission;

import android.os.Build;

import androidx.annotation.RequiresApi;

public interface PermissionReceiver {

    /**
     * An action to perform if permission is granted. This should usually result in an appropriate
     * chime being played.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    void onGrant();
}
