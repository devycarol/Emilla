@file:JvmName("Maths")

package net.emilla.math

import androidx.annotation.StringRes
import net.emilla.R
import net.emilla.exception.EmillaException
import java.text.DecimalFormat

// todo: configurable sig digs.
fun prettyNumber(n: Double): String = DecimalFormat("#.######").format(n)

fun tryParseLong(num: String, @StringRes errorTitle: Int) = try {
    num.toLong()
} catch (_: NumberFormatException) {
    throw malformedExpression(errorTitle)
}

fun tryParseDouble(num: String, @StringRes errorTitle: Int) = try {
    num.toDouble()
} catch (_: NumberFormatException) {
    throw malformedExpression(errorTitle)
}

fun malformedExpression(@StringRes errorTitle: Int): EmillaException {
    return EmillaException(errorTitle, R.string.error_calc_malformed_expression)
}

fun undefined(@StringRes errorTitle: Int): EmillaException {
    return EmillaException(errorTitle, R.string.error_calc_undefined)
}

fun Double.factorial(): Double {
    return toLong().factorial().toDouble()
    // TODO: this discards the fractional part. Find the proper way to compute factorial of
    //  fractional numbers
}

fun Long.factorial(): Long {
    if (this < 0L) throw ArithmeticException()
    if (this < 2L) return 1L

    var fact = 2L
    for (i in 3L..this) fact *= i

    return fact
}
