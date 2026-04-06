package net.emilla.command.core;

import android.content.Context;
import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.math.BitwiseCalculator;

import java.math.BigInteger;

final class Bits extends CoreCommand {
    @internal Bits(Context ctx) {
        super(ctx, CoreEntry.BITS, EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected void run(AssistActivity act) {
        CategoryCommand.run(act, Intent.CATEGORY_APP_CALCULATOR);
    }

    @Override
    protected void run(AssistActivity act, String expression) {
        BigInteger result;
        try {
            result = BitwiseCalculator.compute(expression);
        } catch (ArithmeticException __) {
            fail(act, R.string.error_calc_undefined);
            return;
        } catch (NumberFormatException __) {
            fail(act, R.string.error_calc_malformed_expression);
            return;
        }

        giveText(act, result.toString());
    }
}
