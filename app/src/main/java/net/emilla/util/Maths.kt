@file:JvmName("Maths")

package net.emilla.util

import java.text.DecimalFormat

// todo: configurable sig digs.
fun Double.prettyNumber(): String = DecimalFormat("#.######").format(this)
