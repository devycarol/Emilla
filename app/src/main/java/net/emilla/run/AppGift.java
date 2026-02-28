package net.emilla.run;

import android.content.Intent;

import net.emilla.R;
import net.emilla.activity.PassthroughActivity;
import net.emilla.exception.EmillaException;
import net.emilla.util.Intents;

public enum AppGift {;
    public static CommandRun instance(Intent intent) {
        return act -> {
            if (intent.resolveActivity(act.getPackageManager()) != null) {
                act.finishAndRemoveTask();
                act.startActivity(
                    Intents.me(act, PassthroughActivity.class)
                        .putExtra(Intent.EXTRA_INTENT, intent)
                        .addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)

                );
            } else {
                throw new EmillaException(R.string.error, R.string.error_no_app);
            }
        };
    }
}
