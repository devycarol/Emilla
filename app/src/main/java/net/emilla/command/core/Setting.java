package net.emilla.command.core;

import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
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

    private static final class SettingParams extends CoreParams {

        private SettingParams() {
            super(NAME,
                  R.string.instruction_setting,
                  R.drawable.ic_settings,
                  EditorInfo.IME_ACTION_DONE,
                  R.string.summary_setting,
                  R.string.manual_setting);
        }
    }

    public Setting(AssistActivity act) {
        super(act, new SettingParams());
    }

    @Override
    protected void run() {
        throw badCommand(R.string.error_unfinished_setting);
    }

    @Override
    protected void run(@NonNull String query) {
        throw badCommand(R.string.error_unfinished_setting);
    }
}
