package net.emilla.run;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;

import net.emilla.R;

public enum AppSuccess {;
    public static CommandRun instance(Intent intent) {
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        return act -> {
            var pm = act.getPackageManager();
            if (intent.resolveActivity(pm) != null) {
                succeed(act, intent);
            } else try {
                succeed(act, intent);
            } catch (ActivityNotFoundException e) {
                act.fail(R.string.error, R.string.error_no_app);
            }
        };
    }

    private static void succeed(Activity act, Intent intent) {
        act.startActivity(intent);
        act.finishAndRemoveTask();
    }
}
