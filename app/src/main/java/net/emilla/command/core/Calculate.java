package net.emilla.command.core;

import static android.content.Intent.CATEGORY_APP_CALCULATOR;

import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import net.emilla.activity.AssistActivity;
import net.emilla.apps.Apps;
import net.emilla.math.Calculator;
import net.emilla.math.Maths;

/*internal*/ final class Calculate extends CategoryCommand {

    public static final String ENTRY = "calculate";

    public static boolean possible() {
        return true;
    }

    /*internal*/ Calculate(AssistActivity act) {
        super(act, CoreEntry.CALCULATE, EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected Intent makeFilter() {
        return Apps.categoryTask(CATEGORY_APP_CALCULATOR);
    }

    @Override
    protected void run(String expression) {
        giveText(Maths.prettyNumber(Calculator.compute(expression, CoreEntry.CALCULATE.name)));
    }

}
