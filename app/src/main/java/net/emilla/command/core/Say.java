package net.emilla.command.core;

import android.content.Context;
import android.view.inputmethod.EditorInfo;

import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.config.SettingVals;
import net.emilla.speech.TtsFragment;

final class Say extends CoreCommand {
    private final TtsFragment mFragment = TtsFragment.newInstance();

    @internal Say(Context ctx) {
        super(ctx, CoreEntry.SAY, EditorInfo.IME_ACTION_DONE);

        giveGadgets(mFragment);
    }

    private void say(AssistActivity act, String phrase) {
        mFragment.say(phrase);
        act.selectInstruction();
    }

    @Override
    protected void run(AssistActivity act) {
        var prefs = act.getSharedPreferences();
        var res = act.getResources();
        say(act, SettingVals.motd(prefs, res));
    }

    @Override
    protected void run(AssistActivity act, String message) {
        say(act, message);
    }
}
