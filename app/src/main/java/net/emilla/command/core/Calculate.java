package net.emilla.command.core;

import android.content.Context;
import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.math.Calculator;
import net.emilla.math.Maths;

import java.math.BigDecimal;

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
        BigDecimal result;
        try {
            result = Calculator.compute(expression);
        } catch (ArithmeticException __) {
            fail(act, R.string.error_calc_undefined);
            return;
        } catch (NumberFormatException __) {
            fail(act, R.string.error_calc_malformed_expression);
            return;
        }

        giveText(act, Maths.prettyNumber(result));
    }
}
