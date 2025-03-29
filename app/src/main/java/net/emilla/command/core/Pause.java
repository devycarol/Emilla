package net.emilla.command.core;

import android.media.AudioManager;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.util.MediaControl;
import net.emilla.util.Services;

public final class Pause extends CoreCommand {

    public static final String ENTRY = "pause";
    @StringRes
    public static final int NAME = R.string.command_pause;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_pause;

    public static Yielder yielder() {
        return new Yielder(false, Pause::new, ENTRY, NAME, ALIASES);
    }

    public static boolean possible() {
        return true;
    }

    private Pause(AssistActivity act) {
        super(act, NAME,
              R.string.instruction_pause,
              R.drawable.ic_pause,
              R.string.summary_pause,
              R.string.manual_pause,
              EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected void run() {
        AudioManager am = Services.audio(activity);
        MediaControl.sendPauseEvent(am);
        give(() -> {});
    }

    @Override
    protected void run(String ignored) {
        run(); // Todo: remove this from the interface for non-instructables.
    }
}
