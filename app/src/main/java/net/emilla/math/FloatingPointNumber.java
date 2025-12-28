package net.emilla.math;

import androidx.annotation.StringRes;

import net.emilla.annotation.internal;
import net.emilla.math.CalcToken.InfixToken;

final class FloatingPointNumber implements InfixToken {

    public final double value;

    @internal FloatingPointNumber(String number, @StringRes int errorTitle) {
        this.value = Maths.tryParseDouble(number, errorTitle);
    }

}
