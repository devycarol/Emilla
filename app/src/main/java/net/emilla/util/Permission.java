package net.emilla.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.run.PermissionFailure;
import net.emilla.run.PermissionOffering;

public enum Permission {
    CALL(
        R.string.perm_calling,
        Manifest.permission.CALL_PHONE
    ),
    CONTACTS(
        R.string.perm_contacts,

        Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS
    ),
    PINGS(
        R.string.perm_notifications,
        Build.VERSION_CODES.TIRAMISU,
        Manifest.permission.POST_NOTIFICATIONS
    ),
    TASKER(
        R.string.perm_tasker,
        TaskerIntent.PERMISSION_RUN_TASKS
    );

    @StringRes
    private final int mName;
    private final int mRestrictSdk;
    private final String[] mPermissions;

    Permission(@StringRes int name, String... permissions) {
        this(name, Build.VERSION_CODES.M, permissions);
        // in Lollipop, permissions are granted at install
    }

    Permission(@StringRes int name, int restrictSdk, String... permissions) {
        mName = name;
        mRestrictSdk = restrictSdk;
        mPermissions = permissions;
    }

    @SuppressLint("NewApi")
    public void flow(AssistActivity act, @Nullable Runnable onGrant) {
        if (has(act)) {
            return;
        }

        if (isPromptAllowed(act)) {
            act.offer(new PermissionOffering(mPermissions, onGrant));
        } else {
            act.fail(new PermissionFailure(act, mName));
        }
    }

    @SuppressLint("NewApi")
    public final void with(AssistActivity act, Runnable onGrant) {
        if (has(act)) {
            onGrant.run();
            return;
        }

        if (isPromptAllowed(act)) {
            act.offer(new PermissionOffering(mPermissions, onGrant));
        } else {
            act.fail(new PermissionFailure(act, mName));
        }
    }

    @SuppressLint("NewApi")
    public void with(AssistActivity act, Runnable onGrant, Runnable onNoPrompt, Runnable afterGrant) {
        if (has(act)) {
            onGrant.run();
            return;
        }

        if (isPromptAllowed(act)) {
            act.offer(
                new PermissionOffering(mPermissions, () -> {
                    onGrant.run();
                    afterGrant.run();
                })
            );
        } else {
            onNoPrompt.run();
        }
    }

    @SuppressLint("NewApi")
    public boolean has(Context ctx) {
        if (Build.VERSION.SDK_INT < mRestrictSdk) {
            return true;
        }

        for (String permission : mPermissions) {
            if (ctx.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isPromptAllowed(Activity act) {
        for (String permission : mPermissions) {
            if (!act.shouldShowRequestPermissionRationale(permission)) {
                return false;
            }
        }
        return true;
    }

}
