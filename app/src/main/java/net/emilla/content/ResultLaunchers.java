package net.emilla.content;

import android.content.ActivityNotFoundException;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.run.MessageFailure;

public final class ResultLaunchers {

    public static <I> boolean tryLaunch(
        AssistActivity act,
        ActivityResultLauncher<I> launcher,
        @Nullable I input
    ) {
        try {
            launcher.launch(input);
            return true;
        } catch (ActivityNotFoundException e) {
            act.fail(new MessageFailure(act, R.string.error, R.string.error_no_app));
            return false;
        }
    }

    private ResultLaunchers() {}

}
