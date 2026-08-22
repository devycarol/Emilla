package net.emilla.command.core;

import android.content.Context;
import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import net.emilla.Feedback;
import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.command.EmillaCommand;
import net.emilla.math.Calculator;
import net.emilla.math.Maths;
import net.emilla.wadget.ActionSurface;

import java.math.BigDecimal;

final class Calculate extends EmillaCommand {
    @internal Calculate(Context ctx) {
        super(ctx, CoreEntry.CALCULATE, EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected Feedback run(ActionSurface surface, AssistActivity act) {
        return CategoryCommand.run(surface, act, Intent.CATEGORY_APP_CALCULATOR);
    }

    @Override
    protected Feedback run(ActionSurface surface, AssistActivity act, String expression) {
        BigDecimal result;
        try {
            result = Calculator.compute(expression);
        } catch (ArithmeticException __) {
            return Feedback.fail(R.string.error_calc_undefined);
        } catch (NumberFormatException __) {
            return Feedback.fail(R.string.error_calc_malformed_expression);
        }

        return Feedback.giveText(Maths.prettyNumber(result));
    }
}
