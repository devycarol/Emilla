package net.emilla.lang.measure.impl;

import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.exception.EmillaException;
import net.emilla.lang.LatinToken.Letter;
import net.emilla.lang.LatinToken.Word;
import net.emilla.lang.LatinTokens;
import net.emilla.lang.measure.CelsiusConversion;

import java.util.Locale;

public enum CelsiusConversionEN_US {
    ;

    public static CelsiusConversion instance(String s, @StringRes int errorTitle) {
        try {
            var tokens = new LatinTokens(s);
            double degrees = tokens.nextNumber();
            if (tokens.finished()) {
                return new CelsiusConversion(degrees, false);
            }

            tokens.skipFirst(
                new Letter(false, '°', false),
                // todo: degree sign technically isn't applicable to Kelvin
                new Word(true, "degrees", true)
            );
            if (tokens.finished()) {
                return new CelsiusConversion(degrees, false);
            }

            String token = tokens.nextOf(
                new Word(true, "fahrenheit", true),
                new Letter(false, 'f', true),
                new Word(true, "kelvin", true),
                new Letter(false, 'k', true)
            );

            tokens.requireFinished();

            boolean fromKelvin = switch (token.toLowerCase(Locale.ROOT)) {
                case "f", "fahrenheit" -> false;
                case "k", "kelvin" -> true;
                default -> throw new IllegalArgumentException("Invalid temperature unit");
            };

            return new CelsiusConversion(degrees, fromKelvin);
        } catch (IllegalStateException e) {
            throw new EmillaException(errorTitle, R.string.error_bad_temperature);
        }
    }

}
