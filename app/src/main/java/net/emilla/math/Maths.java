package net.emilla.math;

import android.os.Build;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;

public enum Maths {;
    public static String prettyNumber(BigDecimal n) {
        // todo: configurable sig digs.
        return new DecimalFormat("#.######").format(n);
    }

    public static int exactInt(BigInteger n) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return n.intValueExact();
        }

        if (n.bitLength() <= 31) {
            return n.intValue();
        }

        throw new ArithmeticException("BigInteger out of int range");
    }

    public static NumberFormatException malformedExpression() {
        return new NumberFormatException("Malformed expression");
    }

    public static BigInteger stupidFactorial(BigInteger n) {
        if (n.compareTo(BigInteger.ZERO) < 0) {
            throw new ArithmeticException("Can't compute factorial of negative value");
        }

        var factorial = BigInteger.ONE;
        for (var m = BigInteger.valueOf(2); m.compareTo(n) <= 0; m = m.add(BigInteger.ONE)) {
            factorial = factorial.multiply(m);
        }

        return factorial;
    }
}
