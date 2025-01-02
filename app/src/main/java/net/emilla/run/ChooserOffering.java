package net.emilla.run;

import android.app.Activity;
import android.content.Intent;

public class ChooserOffering implements Offering {

    private final Activity mActivity;
    private final Intent mIntent;
    private final int mRequestCode;

    public ChooserOffering(Activity act, Intent intent, int requestCode) {
        mActivity = act;
        mIntent = intent;
        mRequestCode = requestCode;
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
        mActivity.startActivityForResult(mIntent, mRequestCode);
        // Todo: rework the handling of this, resolve deprecation
    }
}
