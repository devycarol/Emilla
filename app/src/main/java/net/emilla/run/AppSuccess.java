package net.emilla.run;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.exception.EmillaException;

public final class AppSuccess implements CommandRun {
    private final Intent mIntent;

    public AppSuccess(Intent intent) {
        mIntent = intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
    }

    @Override
    public void run(AssistActivity act) {
        var pm = act.getPackageManager();
        if (mIntent.resolveActivity(pm) != null) {
            succeed(act, mIntent);
        } else try {
            succeed(act, mIntent);
        } catch (ActivityNotFoundException e) {
            throw new EmillaException(R.string.error, R.string.error_no_app);
        }
    }

    private static void succeed(Activity act, Intent intent) {
        act.startActivity(intent);
        act.finishAndRemoveTask();
    }
}
