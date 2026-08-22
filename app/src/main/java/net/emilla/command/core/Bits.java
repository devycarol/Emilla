package net.emilla.command.core;

import android.content.Context;
import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import net.emilla.Feedback;
import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.command.EmillaCommand;
import net.emilla.math.BitwiseCalculator;
import net.emilla.wadget.ActionSurface;

import java.math.BigInteger;

final class Bits extends EmillaCommand {
    @internal Bits(Context ctx) {
        super(ctx, CoreEntry.BITS, EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected Feedback run(ActionSurface surface, AssistActivity act) {
        return CategoryCommand.run(surface, act, Intent.CATEGORY_APP_CALCULATOR);
    }

    @Override
    protected Feedback run(ActionSurface surface, AssistActivity act, String expression) {
        BigInteger result;
        try {
            result = BitwiseCalculator.compute(expression);
        } catch (ArithmeticException __) {
            return Feedback.fail(R.string.error_calc_undefined);
        } catch (NumberFormatException __) {
            return Feedback.fail(R.string.error_calc_malformed_expression);
        }

        return Feedback.giveText(result.toString());
    }
}
