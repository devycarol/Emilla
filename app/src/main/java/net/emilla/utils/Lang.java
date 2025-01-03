package net.emilla.utils;

import android.content.res.Resources;

import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.command.CmdTokens;

public final class Lang {

    public static CmdTokens cmdTokens(Resources res, String command) {
        return res.getBoolean(R.bool.conf_lang_spaces) ? new CmdTokens.Latin(command)
                : new CmdTokens.Glyph(command);
    }

    public static String wordConcat(Resources res, CharSequence a, CharSequence b) {
        return res.getString(R.string.word_concatenation, a, b);
    }

    public static String wordConcat(Resources res, @StringRes int a, CharSequence b) {
        return res.getString(R.string.word_concatenation, res.getString(a), b);
    }

    public static String wordConcat(Resources res, CharSequence a, @StringRes int b) {
        return res.getString(R.string.word_concatenation, a, res.getString(b));
    }

    public static String wordConcat(Resources res, @StringRes int a, @StringRes int b) {
        return res.getString(R.string.word_concatenation, res.getString(a), res.getString(b));
    }

    public static String colonConcat(Resources res, CharSequence a, CharSequence b) {
        return res.getString(R.string.colon_concatenation, a, b);
    }

    public static String colonConcat(Resources res, @StringRes int a, CharSequence b) {
        return res.getString(R.string.colon_concatenation, res.getString(a), b);
    }

    public static String colonConcat(Resources res, CharSequence a, @StringRes int b) {
        return res.getString(R.string.colon_concatenation, a, res.getString(b));
    }

    public static String colonConcat(Resources res, @StringRes int a, @StringRes int b) {
        return res.getString(R.string.colon_concatenation, res.getString(a), res.getString(b));
    }

    private Lang() {}
}
