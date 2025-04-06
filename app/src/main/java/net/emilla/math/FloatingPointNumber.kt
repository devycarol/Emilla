package net.emilla.math

import androidx.annotation.StringRes
import net.emilla.math.CalcToken.InfixToken

internal class FloatingPointNumber(num: String, @StringRes errorTitle: Int) : InfixToken {
    @JvmField
    val value: Double = tryParseDouble(num, errorTitle)
}
