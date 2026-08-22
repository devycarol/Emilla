package net.emilla.command.core;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.inputmethod.EditorInfo;

import net.emilla.Feedback;
import net.emilla.R;
import net.emilla.annotation.internal;
import net.emilla.command.EmillaCommand;
import net.emilla.setting.SettingMap;
import net.emilla.wadget.ActionSurface;

final class Setting extends EmillaCommand {
    @internal Setting(Context ctx) {
        super(ctx, CoreEntry.SETTING, EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected Feedback run(ActionSurface surface) {
        return Feedback.succeed(new Intent(Settings.ACTION_SETTINGS));
    }

    @Override
    protected Feedback run(ActionSurface surface, String directive) {
        if (true) {
            return Feedback.fail(R.string.error_unfinished_feature);
            // Todo
        }

        var res = surface.getResources();
        var settings = new SettingMap(res);

        var cr = surface.getContentResolver();
        return switch (settings.set(res, cr, directive)) {
            case SUCCESS -> Feedback.give();
            // todo: visually indicate the setting change
            case WAITING -> Feedback.pend();
            case FAILURE -> Feedback.fail(R.string.error_invalid_setting_value);
        };
    }
}
