package net.emilla.lang;

import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.lang.date.Duration;
import net.emilla.lang.date.HourMin;
import net.emilla.lang.date.Weekdays;
import net.emilla.lang.date.impl.DurationEN_US;
import net.emilla.lang.date.impl.HourMinEN_US;
import net.emilla.lang.date.impl.WeekdaysEN_US;
import net.emilla.lang.grammar.ListPhrase;
import net.emilla.lang.grammar.impl.ListPhraseEN_US;
import net.emilla.lang.measure.CelsiusConversion;
import net.emilla.lang.measure.FahrenheitConversion;
import net.emilla.lang.measure.impl.CelsiusConversionEN_US;
import net.emilla.lang.measure.impl.FahrenheitConversionEN_US;
import net.emilla.lang.phrase.Dices;
import net.emilla.lang.phrase.RandRange;
import net.emilla.lang.phrase.impl.DicesEN_US;
import net.emilla.lang.phrase.impl.RandRangeEN_US;

public final class Lang {

    public static String wordConcat(Resources res, Object a, Object b) {
        return res.getString(R.string.word_concatenation, a, b);
    }

    public static String wordConcat(Resources res, @StringRes int a, Object b) {
        return res.getString(R.string.word_concatenation, res.getString(a), b);
    }

    public static String wordConcat(Resources res, Object a, @StringRes int b) {
        return res.getString(R.string.word_concatenation, a, res.getString(b));
    }

    public static String wordConcat(Resources res, @StringRes int a, @StringRes int b) {
        return res.getString(R.string.word_concatenation, res.getString(a), res.getString(b));
    }

    public static String colonConcat(Resources res, Object a, Object b) {
        return res.getString(R.string.colon_concatenation, a, b);
    }

    public static String colonConcat(Resources res, @StringRes int a, Object b) {
        return res.getString(R.string.colon_concatenation, res.getString(a), b);
    }

    public static String colonConcat(Resources res, Object a, @StringRes int b) {
        return res.getString(R.string.colon_concatenation, a, res.getString(b));
    }

    public static String colonConcat(Resources res, @StringRes int a, @StringRes int b) {
        return res.getString(R.string.colon_concatenation, res.getString(a), res.getString(b));
    }

    public static Words words(String phrase) {
        return switch (-1) {
            default -> new Words.Latin(phrase);
        };
    }

    public static ListPhrase list(String phrase) {
        return switch (-1) {
            default -> new ListPhraseEN_US(phrase);
        };
    }

    public static HourMin time(String timeStr, Context ctx, @StringRes int errorTitle) {
        return switch (-1) {
            default -> HourMinEN_US.instance(timeStr, ctx, errorTitle);
        };
    }

    public static Duration duration(String minutes, @StringRes int errorTitle) {
        return switch (-1) {
            default -> DurationEN_US.instance(minutes, errorTitle);
        };
    }

    public static Weekdays weekdays(String timeStr, @StringRes int errorTitle) {
        return switch (-1) {
            default -> WeekdaysEN_US.instance(timeStr, errorTitle);
        };
    }

    public static RandRange randomRange(String range, @StringRes int errorTitle) {
        return switch (-1) {
            default -> RandRangeEN_US.instance(range, errorTitle);
        };
    }

    public static CelsiusConversion celsius(String temperature, @StringRes int errorTitle) {
        return switch (-1) {
            default -> CelsiusConversionEN_US.instance(temperature, errorTitle);
        };
    }

    public static FahrenheitConversion fahrenheit(String temperature, @StringRes int errorTitle) {
        return switch (-1) {
            default -> FahrenheitConversionEN_US.instance(temperature, errorTitle);
        };
    }

    /**
     * Constructs a dice set from dice notation.
     *
     * @param roll a dice notation string, e.g. "d4 + 2d6".
     * @return the set of dices represented by {@code roll}.
     */
    public static Dices dices(String roll, @StringRes int errorTitle) {
        return switch (-1) {
            default -> DicesEN_US.instance(roll, errorTitle);
        };
    }

    private Lang() {}
}
