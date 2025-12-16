package net.emilla.lang;

import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.lang.date.Duration;
import net.emilla.lang.date.HourMin;
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
import net.emilla.trie.PhraseTree;

import java.time.DayOfWeek;
import java.util.EnumSet;
import java.util.function.IntFunction;

public enum Lang {
    EN_US;

    public static String normalize(String s) {
        return s.toLowerCase();
        // Todo lang: strip diacritics and handle the fact that case is really weird
    }

    public static int normalize(int codePoint) {
        return Character.toLowerCase(codePoint);
        // Todo lang: strip diacritics and handle the fact that case is really weird
    }

    public static int parseInt(String s) {
        return Integer.parseInt(s);
    }

    public static long parseLong(String s) {
        return Long.parseLong(s);
    }

    public static float parseFloat(String s) {
        return Float.parseFloat(s);
    }

    public static boolean wordsAreSpaceSeparated(Resources res) {
        return res.getBoolean(R.bool.conf_lang_spaces);
    }

    public static String wordConcat(Resources res, String a, String b) {
        return wordsAreSpaceSeparated(res)
            ? a + ' ' + b
            : a + b;

    }

    public static String wordConcat(Resources res, @StringRes int a, String b) {
        return wordConcat(res, res.getString(a), b);
    }

    public static String wordConcat(Resources res, String a, @StringRes int b) {
        return wordConcat(res, a, res.getString(b));
    }

    public static String wordConcat(Resources res, @StringRes int a, @StringRes int b) {
        return wordConcat(res, res.getString(a), res.getString(b));
    }

    public static String commaConcat(Resources res, Object a, @StringRes int b) {
        return res.getString(R.string.comma_concatenation, a, res.getString(b));
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

    public static <V> PhraseTree<V> phraseTree(Resources res, IntFunction<V[]> arrayGenerator) {
        return PhraseTree.of(wordsAreSpaceSeparated(res), arrayGenerator);
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

    @Nullable
    public static EnumSet<DayOfWeek> weekdays(String timeStr, @StringRes int errorTitle) {
        return switch (-1) {
            default -> WeekdaysEN_US.set(timeStr, errorTitle);
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

    /// Constructs a dice set from dice notation.
    ///
    /// @param roll a dice notation string, e.g. "d4 + 2d6".
    /// @param errorTitle title of the error pop-up for ill-formatted dice.
    /// @return the set of dices represented by `roll`.
    public static Dices dices(String roll, @StringRes int errorTitle) {
        return switch (-1) {
            default -> DicesEN_US.instance(roll, errorTitle);
        };
    }

}
