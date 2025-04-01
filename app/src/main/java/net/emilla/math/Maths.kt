@file:JvmName("Maths")

package net.emilla.math

import androidx.annotation.StringRes
import net.emilla.R
import net.emilla.exception.EmillaException
import java.text.DecimalFormat

// todo: configurable sig digs.
fun prettyNumber(n: Double): String = DecimalFormat("#.######").format(n)

fun tryParseLong(num: String, errorTitle: Int) = try {
    num.toLong()
} catch (_: NumberFormatException) {
    throw malformedExpression(errorTitle)
}

fun tryParseDouble(num: String, errorTitle: Int) = try {
    num.toDouble()
} catch (_: NumberFormatException) {
    throw malformedExpression(errorTitle)
}

fun malformedExpression(@StringRes errorTitle: Int): EmillaException {
    return EmillaException(errorTitle, R.string.error_calc_malformed_expression)
}