package net.emilla.math;

import androidx.annotation.StringRes;

/*internal*/ final class FloatingPointNumber implements InfixToken {

    final double val;

    FloatingPointNumber(String num, @StringRes int errorTitle) {
        val = Maths.tryParseDouble(num, errorTitle);
    }
}
