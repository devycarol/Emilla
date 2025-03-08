package net.emilla.command.core;

import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.settings.Aliases;

public final class Setting extends CoreCommand {

    public static final String ENTRY = "setting";
    @StringRes
    public static final int NAME = R.string.command_setting;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_setting;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static Yielder yielder() {
        return new Yielder(true, Setting::new, ENTRY, NAME, ALIASES);
    }

    public Setting(AssistActivity act) {
        super(act, NAME,
              R.string.instruction_setting,
              R.drawable.ic_settings,
              R.string.summary_setting,
              R.string.manual_setting,
              EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected void run() {
        throw badCommand(R.string.error_unfinished_setting);
    }

    @Override
    protected void run(String query) {
        throw badCommand(R.string.error_unfinished_setting);
    }
}
