@file:JvmName("Maths")

package net.emilla.math

import androidx.annotation.StringRes
import net.emilla.R
import net.emilla.exception.EmillaException
import java.text.DecimalFormat

// todo: configurable sig digs.
fun prettyNumber(n: Double): String = DecimalFormat("#.######").format(n)

fun malformedExpression(@StringRes errorTitle: Int): EmillaException {
    return EmillaException(errorTitle, R.string.error_calc_malformed_expression)
}