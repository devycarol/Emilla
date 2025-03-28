package net.emilla.run;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.Activity;
import android.content.Intent;

import net.emilla.R;
import net.emilla.exception.EmillaException;

public final class AppSuccess implements Success {

    private final Activity mActivity;
    private final Intent mIntent;

    public AppSuccess(Activity act, Intent intent) {
        mActivity = act;
        mIntent = intent;
    }

    @Override
    public void run() {
        var pm = mActivity.getPackageManager();
        if (mIntent.resolveActivity(pm) != null) {
            mActivity.finishAndRemoveTask();
            mActivity.startActivity(mIntent.addFlags(FLAG_ACTIVITY_NEW_TASK));
        } else throw new EmillaException(R.string.error, R.string.error_no_app);
        // Todo: handle these at mapping time. Be mindful of commands with multiple intents.
        //  Handling subcommands will be tricky.
    }
}
