package net.emilla.command.core;

import static android.content.Intent.CATEGORY_APP_CALCULATOR;

import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import net.emilla.activity.AssistActivity;
import net.emilla.math.BitwiseCalculator;
import net.emilla.util.Intents;

/*internal*/ final class Bits extends CategoryCommand {

    /*internal*/ Bits(AssistActivity act) {
        super(act, CoreEntry.BITS, EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected Intent makeFilter() {
        return Intents.categoryTask(CATEGORY_APP_CALCULATOR);
    }

    @Override
    protected void run(AssistActivity act, String expression) {
        giveText(act, String.valueOf(BitwiseCalculator.compute(expression, CoreEntry.BITS.name)));
    }

}
