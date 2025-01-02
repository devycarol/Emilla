package net.emilla.run;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.Activity;
import android.content.Intent;

public class AppSuccess implements Success {

    private final Activity mActivity;
    private final Intent mIntent;

    public AppSuccess(Activity act, Intent intent) {
        mActivity = act;
        mIntent = intent;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        mActivity.finishAndRemoveTask();
        mActivity.startActivity(mIntent.addFlags(FLAG_ACTIVITY_NEW_TASK));
    }
}
