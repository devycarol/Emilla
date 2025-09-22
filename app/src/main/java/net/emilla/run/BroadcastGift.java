package net.emilla.run;

import android.content.Intent;

import net.emilla.activity.AssistActivity;

public final class BroadcastGift implements CommandRun {

    private final Intent mIntent;

    public BroadcastGift(Intent intent) {
        mIntent = intent;
    }

    @Override
    public void run(AssistActivity act) {
        act.sendBroadcast(mIntent);
    }
}
