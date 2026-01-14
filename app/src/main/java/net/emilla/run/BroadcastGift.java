package net.emilla.run;

import android.content.Intent;

public enum BroadcastGift {
    ;
    public static CommandRun instance(Intent intent) {
        return act -> act.sendBroadcast(intent);
    }
}
