package net.emilla.run;

import android.content.Context;
import android.content.Intent;

public final class BroadcastGift implements Runnable {

    private final Context mContext;
    private final Intent mIntent;

    public BroadcastGift(Context ctx, Intent intent) {
        mContext = ctx;
        mIntent = intent;
    }

    @Override
    public void run() {
        mContext.sendBroadcast(mIntent);
    }
}
