package net.emilla.command.core;

import android.view.inputmethod.EditorInfo;

import net.emilla.R;
import net.emilla.activity.AssistActivity;

public final class Setting extends CoreCommand {

    public static final String ENTRY = "setting";

    public static Yielder yielder() {
        return new Yielder(CoreEntry.SETTING, true);
    }

    public static boolean possible() {
        return true;
    }

    /*internal*/ Setting(AssistActivity act) {
        super(act, CoreEntry.SETTING, EditorInfo.IME_ACTION_DONE);
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
