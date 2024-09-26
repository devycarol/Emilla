package net.emilla.utils;

import android.content.res.Resources;

import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.commands.CmdTokens;

public class Lang {
public static CmdTokens cmdTokens(final Resources res, final String command) {
    return res.getBoolean(R.bool.conf_lang_spaces) ? new CmdTokens.Latin(command)
            : new CmdTokens.Glyph(command);
}

public static String wordConcat(final Resources res, final CharSequence a, final CharSequence b) {
    return res.getString(R.string.word_concatenation, a, b);
}

public static String wordConcat(final Resources res, @StringRes final int a, final CharSequence b) {
    return res.getString(R.string.word_concatenation, res.getString(a), b);
}

public static String wordConcat(final Resources res, final CharSequence a, @StringRes final int b) {
    return res.getString(R.string.word_concatenation, a, res.getString(b));
}

public static String wordConcat(final Resources res, @StringRes final int a, @StringRes final int b) {
    return res.getString(R.string.word_concatenation, res.getString(a), res.getString(b));
}

public static String colonConcat(final Resources res, final CharSequence a, final CharSequence b) {
    return res.getString(R.string.colon_concatenation, a, b);
}

public static String colonConcat(final Resources res, @StringRes final int a, final CharSequence b) {
    return res.getString(R.string.colon_concatenation, res.getString(a), b);
}

public static String colonConcat(final Resources res, final CharSequence a, @StringRes final int b) {
    return res.getString(R.string.colon_concatenation, a, res.getString(b));
}

public static String colonConcat(final Resources res, @StringRes final int a, @StringRes final int b) {
    return res.getString(R.string.colon_concatenation, res.getString(a), res.getString(b));
}

private Lang() {}
}
