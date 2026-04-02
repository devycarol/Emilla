package net.emilla.lang;

import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.annotation.Normalized;
import net.emilla.lang.grammar.ListPhrase;
import net.emilla.lang.grammar.impl.ListPhraseEN_US;
import net.emilla.measure.CelsiusConversion;
import net.emilla.measure.FahrenheitConversion;
import net.emilla.measure.impl.CelsiusConversionEN_US;
import net.emilla.measure.impl.FahrenheitConversionEN_US;
import net.emilla.random.DiceRoller;
import net.emilla.time.WallTime;
import net.emilla.trie.PhraseTree;
import net.emilla.util.Int;

import java.util.function.IntFunction;

public enum Lang {
    EN_US,
;
    @Normalized
    public static String normalize(String s) {
        return s.toLowerCase();
        // Todo lang: strip diacritics and handle the fact that case is really weird
    }

    @Normalized
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
            : a + b
        ;
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
        return switch (EN_US) {
            case EN_US -> new ListPhraseEN_US(phrase);
        };
    }

    @Nullable
    public static WallTime wallTime(Context ctx, String time) {
        return switch (EN_US) {
            case EN_US -> EnglishUnitedStates.wallTime(ctx, time);
        };
    }

    @Nullable
    public static Int durationSeconds(Context ctx, String duration) {
        return switch (EN_US) {
            case EN_US -> EnglishUnitedStates.durationSeconds(ctx, duration);
        };
    }

    public static CelsiusConversion celsius(String temperature, @StringRes int errorTitle) {
        return switch (EN_US) {
            case EN_US -> CelsiusConversionEN_US.instance(temperature, errorTitle);
        };
    }

    public static FahrenheitConversion fahrenheit(String temperature, @StringRes int errorTitle) {
        return switch (EN_US) {
            case EN_US -> FahrenheitConversionEN_US.instance(temperature, errorTitle);
        };
    }

    @Nullable
    public static DiceRoller diceRoller(String roll) {
        return switch (EN_US) {
            case EN_US -> EnglishUnitedStates.diceRoller(roll);
        };
    }
}
