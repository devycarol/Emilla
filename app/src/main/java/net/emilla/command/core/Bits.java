package net.emilla.command.core;

import android.content.Intent;

import net.emilla.Feedback;
import net.emilla.R;
import net.emilla.annotation.internal;
import net.emilla.command.EmillaCommand;
import net.emilla.command.ImeAction;
import net.emilla.math.BitwiseCalculator;
import net.emilla.wadget.ActionSurface;

import java.math.BigInteger;

final class Bits extends EmillaCommand {
    @internal Bits(ActionSurface surface) {
        super(surface, CoreEntry.BITS, ImeAction.DO);
    }

    @Override
    protected Feedback run(ActionSurface surface) {
        return CategoryCommand.run(surface, Intent.CATEGORY_APP_CALCULATOR);
    }

    @Override
    protected Feedback run(ActionSurface surface, String expression) {
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
