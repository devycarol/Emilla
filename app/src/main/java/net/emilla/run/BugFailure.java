package net.emilla.run;

import static android.content.Intent.ACTION_SENDTO;
import static android.content.Intent.EXTRA_SUBJECT;
import static android.content.Intent.EXTRA_TEXT;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import net.emilla.BuildConfig;
import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.util.Dialogs;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class BugFailure extends DialogRun {

    private static final String TAG = BugFailure.class.getSimpleName();

    private static AlertDialog.Builder makeDialog(
        AssistActivity act,
        RuntimeException e,
        CharSequence commandName
    ) {
        return Dialogs.dual(act, R.string.error_unknown, R.string.error_bug_report_please,
                R.string.email_bug_report, (dlg, which) -> {
                    emailBugReport(act, e, "unknown error in the " + commandName + " command");
                }).setNeutralButton(R.string.leave, (dlg, which) -> act.cancel());
    }

    private static void emailBugReport(AssistActivity act, RuntimeException e, String errorHeader) {
        String message = e.getMessage();

        var sw = new StringWriter();
        var pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        var stackTrace = sw.toString();

        String body = "Feel free to describe what was happening when the error occurred:\n\n\n\n"
                 + "======== exception details ========\n\n";
        if (!TextUtils.isEmpty(message)) {
            body += message + "\n";
        }
        body += stackTrace + "\n"
                + "======== more helpful stuff ========\n\n"
                + deviceInfo();

        var email = new Intent(ACTION_SENDTO, Uri.parse("mailto:bugs@emilla.net"))
                // TODO: open an actual email account
                .putExtra(EXTRA_SUBJECT, "[Android bug] " + errorHeader)
                .putExtra(EXTRA_TEXT, body);
        act.succeed(new AppSuccess(act, email));
    }

    private static String deviceInfo() {
        var device = new StringBuilder(Build.MODEL.isEmpty() ? "Unknown" : Build.MODEL);
        if (!Build.DEVICE.isEmpty()) {
            device.append(" (").append(Build.DEVICE).append(')');
        }

        String baseOs;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Build.VERSION.BASE_OS.isEmpty()) {
            baseOs = Build.VERSION.BASE_OS;
        } else {
            baseOs = "Android";
        }

        var os = new StringBuilder(baseOs).append(' ');
        if (!Build.VERSION.RELEASE.isEmpty()) {
            os.append(Build.VERSION.RELEASE).append(' ');
        }
        os.append(versionCodename());

        var app = new StringBuilder(BuildConfig.APPLICATION_NAME).append(' ')
                .append(BuildConfig.VERSION_NAME).append(' ')
                .append(BuildConfig.VERSION_CODENAME);
        if (BuildConfig.DEBUG) app.append(" (debug)");

        return "Device || " + device + "\nOS || " + os + "\nApp || " + app;
    }

    private static String versionCodename() {
        return switch (Build.VERSION.SDK_INT) {
            case Build.VERSION_CODES.KITKAT -> "KitKat";
            case Build.VERSION_CODES.KITKAT_WATCH -> "KitKat Watch";
            case Build.VERSION_CODES.LOLLIPOP -> "Lollipop";
            case Build.VERSION_CODES.LOLLIPOP_MR1 -> "Lollipop MR1";
            case Build.VERSION_CODES.M -> "Marshmallow";
            case Build.VERSION_CODES.N -> "Nougat";
            case Build.VERSION_CODES.N_MR1 -> "Nougat MR1";
            case Build.VERSION_CODES.O -> "Oreo";
            case Build.VERSION_CODES.O_MR1 -> "Oreo MR1";
            case Build.VERSION_CODES.P -> "Pie";
            case Build.VERSION_CODES.Q -> "Q";
            case Build.VERSION_CODES.R -> "Red Velvet Cake";
            case Build.VERSION_CODES.S -> "Snow Cone";
            case Build.VERSION_CODES.S_V2 -> "Snow Cone V2";
            case Build.VERSION_CODES.TIRAMISU -> "Tiramisu";
            case Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> "Upside Down Cake";
            case Build.VERSION_CODES.VANILLA_ICE_CREAM -> "Vanilla Ice Cream";
            default -> "API level " + Build.VERSION.SDK_INT;
        };
    }

    public BugFailure(AssistActivity act, RuntimeException e, CharSequence commandName) {
        super(act, makeDialog(act, e, commandName));
        Log.e(TAG, "unknown error in the " + commandName + " command", e);
    }
}
