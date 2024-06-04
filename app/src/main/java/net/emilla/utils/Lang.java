package net.emilla.utils;

import android.content.res.Resources;

import androidx.annotation.StringRes;

import net.emilla.R;

public class Lang {
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
}
