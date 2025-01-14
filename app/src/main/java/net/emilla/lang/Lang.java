package net.emilla.lang;

import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.lang.date.HourMin;
import net.emilla.lang.date.Weekdays;
import net.emilla.lang.date.impl.HourMinEN_US;
import net.emilla.lang.date.impl.WeekdaysEN_US;

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

    public static HourMin time(String timeStr, Context ctx) {
        return switch (-1) {
            default -> HourMinEN_US.instance(timeStr, ctx);
        };
    }

    public static Weekdays weekdays(String timeStr) {
        return switch (-1) {
            default -> WeekdaysEN_US.instance(timeStr);
        };
    }

    private Lang() {}
}
