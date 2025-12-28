package net.emilla.math;

import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.exception.EmillaException;

import java.text.DecimalFormat;

public enum Maths {
    ;

    public static String prettyNumber(double n) {
        // todo: configurable sig digs.
        return new DecimalFormat("#.######").format(n);
    }

    public static long tryParseLong(String num, @StringRes int errorTitle) {
        try {
            return Long.parseLong(num);
        } catch (NumberFormatException e) {
            throw malformedExpression(errorTitle);
        }
    }

    public static double tryParseDouble(String num, @StringRes int errorTitle) {
        try {
            return Double.parseDouble(num);
        } catch (NumberFormatException e) {
            throw malformedExpression(errorTitle);
        }
    }

    public static EmillaException malformedExpression(@StringRes int errorTitle) {
        return new EmillaException(errorTitle, R.string.error_calc_malformed_expression);
    }

    public static EmillaException undefined(@StringRes int errorTitle) {
        return new EmillaException(errorTitle, R.string.error_calc_undefined);
    }

    public static double factorial(double n) {
        return factorial((long) n);
    }

    public static long factorial(long n) {
        if (n < 0L) {
            throw new ArithmeticException("Can't compute factorial of negative value");
        }

        if (n < 2L) {
            return 1L;
        }

        long factorial = 2L;
        for (long l = 3L; l <= n; ++l) {
            factorial *= l;
        }

        return factorial;
    }

}
