package net.emilla.command.core;

import android.content.Context;
import android.view.inputmethod.EditorInfo;

import net.emilla.Feedback;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.command.EmillaCommand;
import net.emilla.config.SettingVals;
import net.emilla.speech.TtsFragment;
import net.emilla.wadget.ActionSurface;

final class Say extends EmillaCommand {
    private final TtsFragment mFragment = TtsFragment.newInstance();

    @internal Say(Context ctx) {
        super(ctx, CoreEntry.SAY, EditorInfo.IME_ACTION_DONE);

        giveGadgets(mFragment);
    }

    @Override
    protected Feedback run(ActionSurface surface, AssistActivity act) {
        var prefs = act.getSharedPreferences();
        var res = act.getResources();
        mFragment.say(SettingVals.motd(prefs, res));
        return Feedback.selectInstruction();
    }

    @Override
    protected Feedback run(ActionSurface surface, AssistActivity act, String message) {
        mFragment.say(message);
        return Feedback.selectInstruction();
    }
}
