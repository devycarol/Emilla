package net.emilla.command.core;

import static net.emilla.chime.Chime.PEND;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.inputmethod.EditorInfo;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.exception.EmillaException;
import net.emilla.setting.SettingMap;

final class Setting extends CoreCommand {

    @internal Setting(Context ctx) {
        super(ctx, CoreEntry.SETTING, EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected void run(AssistActivity act) {
        appSucceed(act, new Intent(Settings.ACTION_SETTINGS));
    }

    @Override
    protected void run(AssistActivity act, String directive) {
        if (true) {
            throw new EmillaException(R.string.error_unfinished_feature);
            // Todo
        }

        var res = act.getResources();
        var settings = new SettingMap(res);

        var cr = act.getContentResolver();
        switch (settings.set(res, cr, directive)) {
        case SUCCESS -> act.give(a -> {});
        // todo: visually indicate the setting change
        case WAITING -> act.chime(PEND);
        case FAILURE -> throw new EmillaException(R.string.error_invalid_setting_value);
        }
    }

}
