package net.emilla.command.core;

import android.view.inputmethod.EditorInfo;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.lang.Lang;
import net.emilla.lang.phrase.Dices;

import java.util.Random;

public final class Roll extends CoreCommand {

    public static final String ENTRY = "roll";

    public static Yielder yielder() {
        return new Yielder(CoreEntry.ROLL, true);
    }

    public static boolean possible() {
        return true;
    }

    /*internal*/ Roll(AssistActivity act) {
        super(act, CoreEntry.ROLL, EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected void run() {
        var rand = new Random();
        giveText(rand.nextBoolean() ? R.string.heads : R.string.tails);
    }

    @Override
    protected void run(String roll) {
        Dices dices = Lang.dices(roll, CoreEntry.ROLL.name);
        var rand = new Random();
        giveText(String.valueOf(dices.roll(rand)));
    }
}
