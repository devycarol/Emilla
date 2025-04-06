package net.emilla.math;

import androidx.annotation.StringRes;

/*internal*/ final class IntegerNumber implements BitwiseToken {

    final long val;

    IntegerNumber(String num, @StringRes int errorTitle) {
        val = Maths.tryParseLong(num, errorTitle);
    }
}
