package net.emilla.command.core;

import static android.provider.Settings.ACTION_SETTINGS;

import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exception.EmlaBadCommandException;

public class Settings extends CoreCommand {

    public static final String ENTRY = "settings";

    private static class SettingsParams extends CoreParams {

        private SettingsParams() {
            super(R.string.command_settings,
                  R.string.instruction_settings,
                  R.drawable.ic_settings,
                  EditorInfo.IME_ACTION_DONE);
        }
    }

    @Override @ArrayRes
    public int details() {
        return R.array.details_settings;
    }

    public Settings(AssistActivity act, String instruct) {
        super(act, instruct, new SettingsParams());
    }

    @Override
    protected void run() {
        appSucceed(new Intent(ACTION_SETTINGS));
    }

    @Override
    protected void run(String query) {
        throw new EmlaBadCommandException(R.string.command_settings, R.string.error_unfinished_settings);
    }
}
