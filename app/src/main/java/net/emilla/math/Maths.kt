@file:JvmName("Maths")

package net.emilla.math

import java.text.DecimalFormat

// todo: configurable sig digs.
fun prettyNumber(n: Double): String = DecimalFormat("#.######").format(n)
