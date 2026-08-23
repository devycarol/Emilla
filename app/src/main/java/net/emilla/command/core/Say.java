package net.emilla.command.core;

import net.emilla.Feedback;
import net.emilla.action.Widget;
import net.emilla.annotation.internal;
import net.emilla.command.EmillaCommand;
import net.emilla.command.ImeAction;
import net.emilla.config.SettingVals;
import net.emilla.speech.TtsFragment;
import net.emilla.wadget.ActionSurface;

final class Say extends EmillaCommand {
    private final TtsFragment mTtsFragment = TtsFragment.newInstance();

    @internal Say(ActionSurface surface) {
        super(surface, CoreEntry.SAY, ImeAction.DO);
    }

    @Override
    protected Widget[] widgets() {
        return new Widget[] {
            mTtsFragment,
        };
    }

    @Override
    protected Feedback run(ActionSurface surface) {
        var prefs = surface.getSharedPreferences();
        var res = surface.getResources();
        mTtsFragment.say(SettingVals.motd(prefs, res));
        return Feedback.selectInstructionSilently();
    }

    @Override
    protected Feedback run(ActionSurface surface, String message) {
        mTtsFragment.say(message);
        return Feedback.selectInstructionSilently();
    }
}
