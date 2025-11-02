package net.emilla.command.core;

import static android.content.Intent.CATEGORY_APP_CALCULATOR;

import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import net.emilla.activity.AssistActivity;
import net.emilla.apps.Apps;
import net.emilla.math.BitwiseCalculator;

public final class Bits extends CategoryCommand {

    public static final String ENTRY = "bits";

    public static Yielder yielder() {
        return new Yielder(CoreEntry.BITS, true);
    }

    public static boolean possible() {
        return true;
    }

    /*internal*/ Bits(AssistActivity act) {
        super(act, CoreEntry.BITS, EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected Intent makeFilter() {
        return Apps.categoryTask(CATEGORY_APP_CALCULATOR);
    }

    @Override
    protected void run(String expression) {
        giveText(String.valueOf(BitwiseCalculator.compute(expression, CoreEntry.BITS.name)));
    }
}
