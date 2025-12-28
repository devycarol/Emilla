package net.emilla.lang.measure.impl;

import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.exception.EmillaException;
import net.emilla.lang.LatinToken.Letter;
import net.emilla.lang.LatinToken.Word;
import net.emilla.lang.LatinTokens;
import net.emilla.lang.measure.FahrenheitConversion;

import java.util.Locale;

public enum FahrenheitConversionEN_US {
    ;

    public static FahrenheitConversion instance(String s, @StringRes int errorTitle) {
        try {
            var tokens = new LatinTokens(s);
            double degrees = tokens.nextNumber();
            if (tokens.finished()) {
                return new FahrenheitConversion(degrees, false);
            }

            tokens.skipFirst(
                new Letter(false, '°', false),
                // todo: degree sign technically isn't applicable to Kelvin
                new Word(true, "degrees", true)
            );
            if (tokens.finished()) {
                return new FahrenheitConversion(degrees, false);
            }

            String token = tokens.nextOf(
                new Word(true, "celsius", true),
                new Letter(false, 'c', true),
                new Word(true, "kelvin", true),
                new Letter(false, 'k', true)
            );

            tokens.requireFinished();

            boolean fromKelvin = switch (token.toLowerCase(Locale.ROOT)) {
                case "c", "celsius" -> false;
                case "k", "kelvin" -> true;
                default -> throw new IllegalArgumentException();
            };

            return new FahrenheitConversion(degrees, fromKelvin);
        } catch (IllegalStateException e) {
            throw new EmillaException(errorTitle, R.string.error_bad_temperature);
        }
    }

}
