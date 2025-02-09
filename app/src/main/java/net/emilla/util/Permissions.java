package net.emilla.util;

import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.WRITE_CONTACTS;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.permission.PermissionReceiver;
import net.emilla.run.PermissionFailure;
import net.emilla.run.PermissionOffering;
import net.emilla.util.app.TaskerIntent;

public final class Permissions {

    public static final String[] CONTACTS = {READ_CONTACTS, WRITE_CONTACTS};
    public static final String TASKER = TaskerIntent.PERMISSION_RUN_TASKS;

    /**
     * <p>
     * Queries whether phone call permission is granted and initiates request flow if it's not.</p>
     * <p>
     * If the system permission request is suppressed, a fail dialog will link the user to the app
     * info screen where they can manually grant permission.</p>
     *
     * @param act is used to perform permission checks and construct dialogs as needed.
     * @param receiver handler for permission retrieval.
     * @return true if calling permission is granted, false if not.
     */
    public static boolean callFlow(AssistActivity act, PermissionReceiver receiver) {
        return flow(act, CALL_PHONE, receiver, R.string.perm_calling);
    }

    /**
     * <p>
     * Queries whether phone call permission is granted and initiates request flow if it's not.</p>
     * <p>
     * If permission is denied and the request dialog suppressed, the {@code onNoPrompt} function
     * will run.</p>
     *
     * @param act is used to perform permission checks and requests as needed.
     * @param receiver handler for permission retrieval.
     * @param onNoPrompt action to perform if permission is denied and can't be requested.
     * @return true if calling permission is granted, false if not.
     */
    public static boolean callFlow(AssistActivity act, @Nullable PermissionReceiver receiver,
            Runnable onNoPrompt) {
        return flow(act, CALL_PHONE, receiver, onNoPrompt);
    }

    /**
     * Queries whether phone call permission is granted.
     *
     * @param ctx is used to perform permission checks.
     * @return true if calling permission is granted, false if not.
     * @see Permissions#callFlow(AssistActivity, PermissionReceiver)
     */
    public static boolean call(Context ctx) {
        return has(ctx, CONTACTS);
    }

    /**
     * <p>
     * Queries whether the phone call permission prompt is available.</p>
     * <p>
     * This should only be used when permission isn't granted.</p>
     *
     * @param act is used to perform permission checks.
     * @return true if the call prompt is available, false otherwise.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean callPrompt(Activity act) {
        return prompt(act, CALL_PHONE);
    }

    /**
     * <p>
     * Queries whether read/write contacts permission is granted and initiates request flow if it's
     * not.</p>
     * <p>
     * If the system permission request is suppressed, a fail dialog will link the user to the app
     * info screen where they can manually grant permission.</p>
     *
     * @param act is used to perform permission checks and construct dialogs as needed.
     * @param receiver handler for permission retrieval.
     * @return true if contacts permission is granted, false if not.
     */
    public static boolean contactsFlow(AssistActivity act, @Nullable PermissionReceiver receiver) {
        return flow(act, CONTACTS, receiver, R.string.perm_contacts);
    }

    /**
     * <p>
     * Queries whether read/write contacts permission is granted and initiates request flow if it's
     * not.</p>
     * <p>
     * If permission is denied and the request dialog suppressed, the {@code onNoPrompt} function
     * will run.</p>
     *
     * @param act is used to perform permission checks and requests as needed.
     * @param receiver handler for permission retrieval.
     * @param onNoPrompt action to perform if permission is denied and can't be requested.
     * @return true if contacts permission is granted, false if not.
     */
    public static boolean contactsFlow(AssistActivity act, @Nullable PermissionReceiver receiver,
            Runnable onNoPrompt) {
        return flow(act, CONTACTS, receiver, onNoPrompt);
    }

    /**
     * Queries whether read/write contacts permission is granted.
     *
     * @param ctx is used to perform permission checks.
     * @return true if contacts permission is granted, false if not.
     * @see Permissions#contactsFlow(AssistActivity, PermissionReceiver)
     */
    public static boolean contacts(Context ctx) {
        return has(ctx, CONTACTS);
    }

    /**
     * <p>
     * Queries whether the read/write contacts permission prompt is available.</p>
     * <p>
     * This should only be used when permission isn't granted.</p>
     *
     * @param act is used to perform permission checks.
     * @return true if the contacts prompt is available, false otherwise.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean contactsPrompt(Activity act) {
        return prompt(act, CONTACTS);
    }

    /**
     * <p>
     * Queries whether run Tasker tasks permission is granted and initiates request flow if it's not.</p>
     * <p>
     * If the system permission request is suppressed, a fail dialog will link the user to the app
     * info screen where they can manually grant permission.</p>
     *
     * @param act is used to perform permission checks and construct dialogs as needed.
     * @param receiver handler for permission retrieval.
     * @return true if Tasker permission is granted, false if not.
     */
    public static boolean taskerFlow(AssistActivity act, @Nullable PermissionReceiver receiver) {
        return flow(act, TASKER, receiver, R.string.perm_tasker);
    }

    /**
     * <p>
     * Queries whether run Tasker tasks permission is granted and initiates request flow if it's not.</p>
     * <p>
     * If permission is denied and the request dialog suppressed, the {@code onNoPrompt} function
     * will run.</p>
     *
     * @param act is used to perform permission checks and requests as needed.
     * @param receiver handler for permission retrieval.
     * @param onNoPrompt action to perform if permission is denied and can't be requested.
     * @return true if Tasker permission is granted, false if not.
     */
    public static boolean taskerFlow(AssistActivity act, @Nullable PermissionReceiver receiver,
            Runnable onNoPrompt) {
        return flow(act, TASKER, receiver, onNoPrompt);
    }

    /**
     * Queries whether run Tasker tasks permission is granted.
     *
     * @param ctx is used to perform permission checks.
     * @return true if Tasker permission is granted, false if not.
     * @see Permissions#contactsFlow(AssistActivity, PermissionReceiver)
     */
    public static boolean tasker(Context ctx) {
        return has(ctx, TASKER);
    }

    /**
     * <p>
     * Queries whether the run Tasker tasks permission prompt is available.</p>
     * <p>
     * This should only be used when permission isn't granted.</p>
     *
     * @param act is used to perform permission checks.
     * @return true if the Tasker prompt is available, false otherwise.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean taskerPrompt(Activity act) {
        return prompt(act, TASKER);
    }

    private static boolean flow(AssistActivity act, String permission,
            @Nullable PermissionReceiver receiver, @StringRes int name) {
        return flow(act, permission, receiver, () -> act.fail(new PermissionFailure(act, name)));
    }

    @SuppressLint("NewApi")
    private static boolean flow(AssistActivity act, String permission,
            @Nullable PermissionReceiver receiver, Runnable onNoPrompt) {
        if (has(act, permission)) return true;

        if (prompt(act, permission)) act.offer(new PermissionOffering(act, permission, receiver));
        else onNoPrompt.run();

        return false;
    }

    private static boolean has(Context ctx, String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
            || ctx.checkSelfPermission(permission) == PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static boolean prompt(Activity act, String permission) {
        return act.shouldShowRequestPermissionRationale(permission);
    }

    private static boolean flow(AssistActivity act, String[] permissions,
            @Nullable PermissionReceiver receiver, @StringRes int name) {
        return flow(act, permissions, receiver, () -> act.fail(new PermissionFailure(act, name)));
    }

    @SuppressLint("NewApi")
    private static boolean flow(AssistActivity act, String[] permissions,
            @Nullable PermissionReceiver receiver, Runnable onNoPrompt) {
        if (has(act, permissions)) return true;

        if (prompt(act, permissions)) act.offer(new PermissionOffering(act, permissions, receiver));
        else onNoPrompt.run();

        return false;
    }

    private static boolean has(Context ctx, String[] permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
        for (String perm : permissions) {
            if (ctx.checkSelfPermission(perm) != PERMISSION_GRANTED) return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static boolean prompt(Activity act, String[] permissions) {
        for (String perm : permissions) {
            if (!act.shouldShowRequestPermissionRationale(perm)) return false;
        }
        return true;
    }

    private Permissions() {}
}
