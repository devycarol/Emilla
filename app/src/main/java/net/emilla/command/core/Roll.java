package net.emilla.command.core;

import android.view.inputmethod.EditorInfo;

import net.emilla.Feedback;
import net.emilla.R;
import net.emilla.annotation.internal;
import net.emilla.command.EmillaCommand;
import net.emilla.lang.Lang;
import net.emilla.random.DiceRoller;
import net.emilla.wadget.ActionSurface;

import java.util.Random;

final class Roll extends EmillaCommand {
    @internal Roll(ActionSurface surface) {
        super(surface, CoreEntry.ROLL, EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected Feedback run(ActionSurface surface) {
        var rand = new Random();
        return Feedback.giveText(
            rand.nextBoolean()
                ? R.string.heads
                : R.string.tails
            //
        );
    }

    @Override
    protected Feedback run(ActionSurface surface, String roll) {
        var roller = DiceRoller.of(Lang.EN_US, roll);
        if (roller == null) {
            return Feedback.fail(R.string.error_invalid_dice_roll);
        }

        var rand = new Random();
        return Feedback.giveText(String.valueOf(roller.roll(rand)));
    }
}
