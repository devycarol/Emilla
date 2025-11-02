package net.emilla.run;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;

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
        PackageManager pm = act.getPackageManager();
        if (mIntent.resolveActivity(pm) != null) {
            act.finishAndRemoveTask();
            act.startActivity(mIntent);
        } else try {
            act.startActivity(mIntent);
            act.finishAndRemoveTask();
        } catch (ActivityNotFoundException e) {
            throw new EmillaException(R.string.error, R.string.error_no_app);
        }
    }
}
