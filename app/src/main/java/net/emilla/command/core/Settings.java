package net.emilla.command.core;

import static android.provider.Settings.ACTION_SETTINGS;

import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exception.EmlaBadCommandException;
import net.emilla.settings.Aliases;

public class Settings extends CoreCommand {

    public static final String ENTRY = "settings";
    @StringRes
    public static final int NAME = R.string.command_settings;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_settings;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static Yielder yielder() {
        return new Yielder(true, Settings::new, ENTRY, NAME, ALIASES);
    }

    private static class SettingsParams extends CoreParams {

        private SettingsParams() {
            super(NAME,
                  R.string.instruction_settings,
                  R.drawable.ic_settings,
                  EditorInfo.IME_ACTION_DONE,
                  R.string.summary_settings,
                  R.string.manual_settings);
        }
    }

    public Settings(AssistActivity act) {
        super(act, new SettingsParams());
    }

    @Override
    protected void run() {
        appSucceed(new Intent(ACTION_SETTINGS));
    }

    @Override
    protected void run(String query) {
        throw new EmlaBadCommandException(NAME, R.string.error_unfinished_settings);
    }
}
