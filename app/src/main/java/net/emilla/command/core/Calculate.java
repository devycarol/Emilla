package net.emilla.command.core;

import android.content.Context;
import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.math.Calculator;
import net.emilla.math.Maths;

final class Calculate extends CoreCommand {
    @internal Calculate(Context ctx) {
        super(ctx, CoreEntry.CALCULATE, EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected void run(AssistActivity act) {
        CategoryCommand.run(act, Intent.CATEGORY_APP_CALCULATOR);
    }

    @Override
    protected void run(AssistActivity act, String expression) {
        giveText(act, Maths.prettyNumber(Calculator.compute(expression, CoreEntry.CALCULATE.name)));
    }
}
