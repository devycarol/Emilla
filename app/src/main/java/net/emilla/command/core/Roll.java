package net.emilla.command.core;

import android.content.Context;
import android.view.inputmethod.EditorInfo;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.lang.Lang;
import net.emilla.random.DiceRoller;

import java.util.Random;

final class Roll extends CoreCommand {
    @internal Roll(Context ctx) {
        super(ctx, CoreEntry.ROLL, EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected void run(AssistActivity act) {
        var rand = new Random();
        giveText(act, rand.nextBoolean() ? R.string.heads : R.string.tails);
    }

    @Override
    protected void run(AssistActivity act, String roll) {
        DiceRoller roller = Lang.diceRoller(roll);
        if (roller == null) {
            fail(act, R.string.error_invalid_dice_roll);
            return;
        }

        var rand = new Random();
        giveText(act, String.valueOf(roller.roll(rand)));
    }
}
