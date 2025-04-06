package net.emilla.math

import androidx.annotation.StringRes
import net.emilla.math.CalcToken.BitwiseToken

internal class IntegerNumber(num: String, @StringRes errorTitle: Int) : BitwiseToken {
    @JvmField
    val value: Long = tryParseLong(num, errorTitle)
}
