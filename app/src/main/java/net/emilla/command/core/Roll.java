package net.emilla.command.core;

import android.content.Context;
import android.view.inputmethod.EditorInfo;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.lang.Lang;
import net.emilla.lang.phrase.Dices;

import java.util.Random;

/*internal*/ final class Roll extends CoreCommand {

    /*internal*/ Roll(Context ctx) {
        super(ctx, CoreEntry.ROLL, EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected void run(AssistActivity act) {
        var rand = new Random();
        giveText(act, rand.nextBoolean() ? R.string.heads : R.string.tails);
    }

    @Override
    protected void run(AssistActivity act, String roll) {
        Dices dices = Lang.dices(roll, CoreEntry.ROLL.name);
        var rand = new Random();
        giveText(act, String.valueOf(dices.roll(rand)));
    }

}
