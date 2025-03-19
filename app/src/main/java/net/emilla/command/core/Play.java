package net.emilla.command.core;

import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.settings.Aliases;

public final class Play extends CoreCommand {

    public static final String ENTRY = "play";
    @StringRes
    public static final int NAME = R.string.command_play;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_play;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static Yielder yielder() {
        return new Yielder(true, Play::new, ENTRY, NAME, ALIASES);
    }

    public Play(AssistActivity act) {
        super(act, NAME,
              R.string.instruction_play,
              R.drawable.ic_play,
              R.string.summary_play,
              R.string.manual_play,
              EditorInfo.IME_ACTION_GO);
    }

    @Override
    protected void run() {
        throw badCommand(R.string.error_unfinished_feature);
    }

    @Override
    protected void run(String media) {
        throw badCommand(R.string.error_unfinished_feature);
    }
}
