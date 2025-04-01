package net.emilla.lang.measure.impl

import androidx.annotation.StringRes
import net.emilla.R
import net.emilla.exception.EmillaException
import net.emilla.lang.LatinToken.Letter
import net.emilla.lang.LatinToken.Word
import net.emilla.lang.LatinTokens
import net.emilla.lang.measure.FahrenheitConversion

object FahrenheitConversionEN_US {

    @JvmStatic
    fun instance(s: String, @StringRes errorTitle: Int): FahrenheitConversion { try {
        val tokens = LatinTokens(s)

        val degrees = tokens.nextNumber()
        if (tokens.finished()) return FahrenheitConversion(degrees, false)

        tokens.skipFirst(arrayOf(
            Letter(false, '°', false),
            // todo: degree sign technically isn't applicable to Kelvin
            Word(true, "degrees", true),
        ))
        if (tokens.finished()) return FahrenheitConversion(degrees, false)

        val token: String = tokens.nextOf(arrayOf(
            Word(true, "celsius", true),
            Letter(false, 'c', true),
            Word(true, "kelvin", true),
            Letter(false, 'k', true),
        ))

        tokens.requireFinished()

        val isKelvin = when (token.lowercase()) {
            "c", "celsius" -> false
            "k", "kelvin" -> true
            else -> throw IllegalArgumentException()
        }

        return FahrenheitConversion(degrees, isKelvin)
    } catch (_: IllegalStateException) {
        throw EmillaException(errorTitle, R.string.error_bad_temperature)
    }}
}
