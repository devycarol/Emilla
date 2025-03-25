package net.emilla.math;

import static net.emilla.math.Maths.malformedExpression;
import static java.lang.Double.parseDouble;

import androidx.annotation.StringRes;

/*internal*/ final class FloatingPointNumber implements InfixToken {

    final double val;

    FloatingPointNumber(String num, @StringRes int errorTitle) { try {
        val = parseDouble(num);
    } catch (NumberFormatException e) {
        throw malformedExpression(errorTitle);
    }}
}
