package net.emilla.math;

import androidx.annotation.StringRes;

import net.emilla.annotation.internal;
import net.emilla.math.CalcToken.BitwiseToken;

final class IntegerNumber implements BitwiseToken {

    public final long value;

    @internal
    IntegerNumber(String num, @StringRes int errorTitle) {
        this.value = Maths.tryParseLong(num, errorTitle);
    }

}
