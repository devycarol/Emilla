package net.emilla.lang;

import android.content.res.Resources;

import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.command.CmdTokens;
import net.emilla.lang.date.TimeParser;
import net.emilla.lang.date.WeekdayParser;
import net.emilla.lang.date.impl.TimeParserEN_US;
import net.emilla.lang.date.impl.WeekdayParserEN_US;

public final class Lang {

    public static CmdTokens cmdTokens(Resources res, String command) {
        return res.getBoolean(R.bool.conf_lang_spaces) ? new CmdTokens.Latin(command)
                : new CmdTokens.Glyph(command);
    }

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

    public static TimeParser timeParser(String timeStr) {
        return switch (-1) {
            default -> TimeParserEN_US.instance(timeStr);
        };
    }

    public static WeekdayParser weekdayParser(String timeStr) {
        return switch (-1) {
            default -> WeekdayParserEN_US.instance(timeStr);
        };
    }

    private Lang() {}
}
