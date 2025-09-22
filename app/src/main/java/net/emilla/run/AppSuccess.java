package net.emilla.run;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;

import net.emilla.R;
import net.emilla.exception.EmillaException;

public final class AppSuccess implements Runnable {

    private final Activity mActivity;
    private final Intent mIntent;

    public AppSuccess(Activity act, Intent intent) {
        mActivity = act;
        mIntent = intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
    }

    @Override
    public void run() {
        var pm = mActivity.getPackageManager();
        if (mIntent.resolveActivity(pm) != null) {
            mActivity.finishAndRemoveTask();
            mActivity.startActivity(mIntent);
        } else try {
            mActivity.startActivity(mIntent);
            mActivity.finishAndRemoveTask();
        } catch (ActivityNotFoundException e) {
            throw new EmillaException(R.string.error, R.string.error_no_app);
        }
    }
}
