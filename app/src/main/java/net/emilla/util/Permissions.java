package net.emilla.util;

import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.POST_NOTIFICATIONS;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.WRITE_CONTACTS;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.app.Activity;
import android.content.Context;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.run.PermissionFailure;
import net.emilla.run.PermissionOffering;
import net.emilla.util.app.TaskerIntent;

public final class Permissions {

    public static final String CALL = CALL_PHONE;
    public static final String[] CONTACTS = {READ_CONTACTS, WRITE_CONTACTS};
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static final String PINGS = POST_NOTIFICATIONS;
    public static final String TASKER = TaskerIntent.PERMISSION_RUN_TASKS;

    /**
     * <p>
     * Performs the given action if phone call permission is granted and triggers request flow if
     * not. If the user then grants permission, the action will be performed at that point.</p>
     * <p>
     * If the system permission request is suppressed, a fail dialog will link the user to the app
     * info screen where they can manually grant permission.</p>
     *
     * @param act is used to perform permission checks and construct dialogs as needed.
     * @param onGrant action to perform if permission granted.
     */
    public static void withCall(AssistActivity act, Runnable onGrant) {
        if (call(act)) {
            onGrant.run();
            return;
        }

        if (callPrompt(act)) act.offer(new PermissionOffering(act, CALL, onGrant));
        else act.fail(new PermissionFailure(act, R.string.perm_calling));
    }

    /**
     * <p>
     * Checks if phone call permission is granted and triggers request flow if not.</p>
     * <p>
     * If the system permission request is suppressed, a fail dialog will link the user to the app
     * info screen where they can manually grant permission.</p>
     *
     * @param act is used to perform permission checks and construct dialogs as needed.
     * @param onGrant optional action to perform if permission granted.
     * @return true if calling permission is granted, false if not.
     */
    public static boolean callFlow(AssistActivity act, @Nullable Runnable onGrant) {
        if (call(act)) return true;

        if (callPrompt(act)) act.offer(new PermissionOffering(act, CALL, onGrant));
        else act.fail(new PermissionFailure(act, R.string.perm_calling));

        return false;
    }

    /**
     * <p>
     * Checks if phone call permission is granted and triggers request flow if not.</p>
     * <p>
     * If permission is denied and the request dialog suppressed, the {@code onNoPrompt} function
     * will run.</p>
     *
     * @param act is used to perform permission checks and requests as needed.
     * @param onGrant optional action to perform if permission granted.
     * @param onNoPrompt action to perform if permission is denied and can't be requested.
     * @return true if calling permission is granted, false if not.
     */
    public static boolean callFlow(
        AssistActivity act,
        @Nullable Runnable onGrant,
        Runnable onNoPrompt
    ) {
        if (call(act)) return true;

        if (callPrompt(act)) act.offer(new PermissionOffering(act, CALL, onGrant));
        else onNoPrompt.run();

        return false;
    }

    /**
     * Checks if phone call permission is granted.
     *
     * @param ctx is used to perform permission checks.
     * @return true if calling permission is granted, false if not.
     * @see Permissions#callFlow(AssistActivity, Runnable)
     */
    public static boolean call(Context ctx) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
            || ctx.checkSelfPermission(CALL) == PERMISSION_GRANTED;
    }

    /**
     * <p>
     * Checks if the phone call permission prompt is available.</p>
     * <p>
     * This should only be used when permission isn't granted.</p>
     *
     * @param act is used to perform permission checks.
     * @return true if the call prompt is available, false otherwise.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private static boolean callPrompt(Activity act) {
        return prompt(act, CALL);
    }

    /**
     * <p>
     * Performs the given action if read/write contacts permission is granted and triggers request
     * flow if not. If the user then grants permission, the action will be performed at that point.</p>
     * <p>
     * If the system permission request is suppressed, a fail dialog will link the user to the app
     * info screen where they can manually grant permission.</p>
     *
     * @param act is used to perform permission checks and construct dialogs as needed.
     * @param onGrant action to perform if permission granted.
     */
    public static void withContacts(AssistActivity act, Runnable onGrant) {
        if (contacts(act)) {
            onGrant.run();
            return;
        }

        if (contactsPrompt(act)) act.offer(new PermissionOffering(act, CONTACTS, onGrant));
        else act.fail(new PermissionFailure(act, R.string.perm_contacts));
    }

    /**
     * <p>
     * Performs the given action if read/write contacts permission is granted and triggers request
     * flow if not. If the user then grants permission, the action will be performed at that point.</p>
     * <p>
     * If permission is denied and the request dialog suppressed, the {@code onNoPrompt} function
     * will run.</p>
     *
     * @param act is used to perform permission checks and construct dialogs as needed.
     * @param onGrant action to perform if permission granted.
     * @param onNoPrompt action to perform if permission is denied and can't be requested.
     * @param afterGrant action to perform if permission is requested and granted.
     */
    public static void withContacts(
        AssistActivity act,
        Runnable onGrant,
        Runnable onNoPrompt,
        Runnable afterGrant
    ) {
        if (contacts(act)) {
            onGrant.run();
            return;
        }

        if (contactsPrompt(act)) act.offer(new PermissionOffering(act, CONTACTS, () -> {
            onGrant.run();
            afterGrant.run();
        }));
        else onNoPrompt.run();
    }

    /**
     * <p>
     * Checks if read/write contacts permission is granted and triggers request flow if not.</p>
     * <p>
     * If the system permission request is suppressed, a fail dialog will link the user to the app
     * info screen where they can manually grant permission.</p>
     *
     * @param act is used to perform permission checks and construct dialogs as needed.
     * @param onGrant optional action to perform if permission granted.
     * @return true if contacts permission is granted, false if not.
     */
    public static boolean contactsFlow(AssistActivity act, @Nullable Runnable onGrant) {
        if (contacts(act)) return true;

        if (contactsPrompt(act)) act.offer(new PermissionOffering(act, CONTACTS, onGrant));
        else act.fail(new PermissionFailure(act, R.string.perm_contacts));

        return false;
    }

    /**
     * <p>
     * Checks if read/write contacts permission is granted and triggers request flow if not.</p>
     * <p>
     * If permission is denied and the request dialog suppressed, the {@code onNoPrompt} function
     * will run.</p>
     *
     * @param act is used to perform permission checks and requests as needed.
     * @param onGrant optional action to perform if permission granted.
     * @param onNoPrompt action to perform if permission is denied and can't be requested.
     * @return true if contacts permission is granted, false if not.
     */
    public static boolean contactsFlow(
        AssistActivity act,
        @Nullable Runnable onGrant,
        Runnable onNoPrompt
    ) {
        if (contacts(act)) return true;

        if (contactsPrompt(act)) act.offer(new PermissionOffering(act, CONTACTS, onGrant));
        else onNoPrompt.run();

        return false;
    }

    /**
     * Checks if read/write contacts permission is granted.
     *
     * @param ctx is used to perform permission checks.
     * @return true if contacts permission is granted, false if not.
     * @see Permissions#contactsFlow(AssistActivity, Runnable)
     */
    public static boolean contacts(Context ctx) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
            || ctx.checkSelfPermission(READ_CONTACTS) == PERMISSION_GRANTED
            && ctx.checkSelfPermission(WRITE_CONTACTS) == PERMISSION_GRANTED;
    }

    /**
     * <p>
     * Checks if the read/write contacts permission prompt is available.</p>
     * <p>
     * This should only be used when permission isn't granted.</p>
     *
     * @param act is used to perform permission checks.
     * @return true if the contacts prompt is available, false otherwise.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private static boolean contactsPrompt(Activity act) {
        return prompt(act, READ_CONTACTS)
            && prompt(act, WRITE_CONTACTS);
    }

    /**
     * <p>
     * Performs the given action if notifications permission is granted and triggers request flow if
     * not. If the user then grants permission, the action will be performed at that point.</p>
     * <p>
     * If the system permission request is suppressed, a fail dialog will link the user to the app
     * info screen where they can manually grant permission.</p>
     *
     * @param act is used to perform permission checks and construct dialogs as needed.
     * @param onGrant action to perform if permission granted.
     */
    public static void withPings(AssistActivity act, Runnable onGrant) {
        if (pings(act)) {
            onGrant.run();
            return;
        }

        if (pingsPrompt(act)) act.offer(new PermissionOffering(act, PINGS, onGrant));
        else act.fail(new PermissionFailure(act, R.string.perm_notifications));
    }

    /**
     * Checks if notifications permission is granted.
     *
     * @param ctx is used to perform permission checks.
     * @return true if contacts permission is granted, false if not.
     */
    public static boolean pings(Context ctx) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
            || ctx.checkSelfPermission(PINGS) == PERMISSION_GRANTED;
    }

    /**
     * <p>
     * Checks if the notifications permission prompt is available.</p>
     * <p>
     * This should only be used when permission isn't granted.</p>
     *
     * @param act is used to perform permission checks.
     * @return true if the contacts prompt is available, false otherwise.
     */
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private static boolean pingsPrompt(Activity act) {
        return prompt(act, PINGS);
    }

    /**
     * <p>
     * Checks if run Tasker tasks permission is granted and triggers request flow if not.</p>
     * <p>
     * If the system permission request is suppressed, a fail dialog will link the user to the app
     * info screen where they can manually grant permission.</p>
     *
     * @param act is used to perform permission checks and construct dialogs as needed.
     * @param onGrant optional action to perform if permission granted.
     * @return true if Tasker permission is granted, false if not.
     */
    public static boolean taskerFlow(AssistActivity act, @Nullable Runnable onGrant) {
        if (tasker(act)) return true;

        if (taskerPrompt(act)) act.offer(new PermissionOffering(act, TASKER, onGrant));
        else act.fail(new PermissionFailure(act, R.string.perm_tasker));

        return false;
    }

    /**
     * <p>
     * Checks if run Tasker tasks permission is granted and triggers request flow if not.</p>
     * <p>
     * If permission is denied and the request dialog suppressed, the {@code onNoPrompt} function
     * will run.</p>
     *
     * @param act is used to perform permission checks and requests as needed.
     * @param onGrant optional action to perform if permission granted.
     * @param onNoPrompt action to perform if permission is denied and can't be requested.
     * @return true if Tasker permission is granted, false if not.
     */
    public static boolean taskerFlow(
        AssistActivity act,
        @Nullable Runnable onGrant,
        Runnable onNoPrompt
    ) {
        if (tasker(act)) return true;

        if (taskerPrompt(act)) act.offer(new PermissionOffering(act, TASKER, onGrant));
        else onNoPrompt.run();

        return false;
    }

    /**
     * Checks if run Tasker tasks permission is granted.
     *
     * @param ctx is used to perform permission checks.
     * @return true if Tasker permission is granted, false if not.
     * @see Permissions#taskerFlow(AssistActivity, Runnable)
     */
    public static boolean tasker(Context ctx) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
            || ctx.checkSelfPermission(TASKER) == PERMISSION_GRANTED;
    }

    /**
     * <p>
     * Checks if the run Tasker tasks permission prompt is available.</p>
     * <p>
     * This should only be used when permission isn't granted.</p>
     *
     * @param act is used to perform permission checks.
     * @return true if the Tasker prompt is available, false otherwise.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private static boolean taskerPrompt(Activity act) {
        return prompt(act, TASKER);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static boolean prompt(Activity act, String permission) {
        return act.shouldShowRequestPermissionRationale(permission);
    }

    private Permissions() {}
}
