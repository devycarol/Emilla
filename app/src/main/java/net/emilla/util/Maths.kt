@file:JvmName("Maths")

package net.emilla.util

import java.text.DecimalFormat

fun Double.prettyNumber(): String {
    // todo: configurable sig digs.
    return DecimalFormat("#.######").format(this)
}
