package net.emilla.math;

import static net.emilla.math.Maths.malformedExpression;
import static java.lang.Long.parseLong;

import androidx.annotation.StringRes;

/*internal*/ final class IntegerNumber implements BitwiseToken {

    final long val;

    IntegerNumber(String num, @StringRes int errorTitle) { try {
        val = parseLong(num);
    } catch (NumberFormatException e) {
        throw malformedExpression(errorTitle);
    }}
}
