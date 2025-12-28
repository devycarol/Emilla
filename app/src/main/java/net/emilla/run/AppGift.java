package net.emilla.run;

import android.content.Intent;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.activity.PassthroughActivity;
import net.emilla.exception.EmillaException;
import net.emilla.util.Intents;

public final class AppGift implements CommandRun {

    private final Intent mIntent;

    public AppGift(Intent intent) {
        mIntent = intent;
    }

    @Override
    public void run(AssistActivity act) {
        if (mIntent.resolveActivity(act.getPackageManager()) != null) {
            act.finishAndRemoveTask();
            act.startActivity(
                Intents.me(act, PassthroughActivity.class)
                    .putExtra(Intent.EXTRA_INTENT, mIntent)
                    .addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            );
        } else {
            throw new EmillaException(R.string.error, R.string.error_no_app);
        }
    }

}
