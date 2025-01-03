package net.emilla.command.core;

import static android.provider.Settings.ACTION_SETTINGS;

import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.DrawableRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exception.EmlaAppsException;

public class Settings extends CoreCommand {

    private final Intent mIntent = new Intent(ACTION_SETTINGS);

    @Override @ArrayRes
    public int detailsId() {
        return R.array.details_settings;
    }

    @Override @DrawableRes
    public int icon() {
        return R.drawable.ic_settings;
    }

    @Override
    public int imeAction() {
        return EditorInfo.IME_ACTION_DONE;
    }

    public Settings(AssistActivity act, String instruct) {
        super(act, instruct, R.string.command_settings, R.string.instruction_settings);
    }

    @Override
    protected void run() {
        if (mIntent.resolveActivity(pm) == null) throw new EmlaAppsException("No app found for system settings."); // todo handle at mapping
        appSucceed(mIntent);
    }

    @Override
    protected void run(String query) {
        // TODO: settings search and value-changing
        run();
    }
}
