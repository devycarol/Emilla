package net.emilla.util;

import androidx.annotation.Nullable;

import net.emilla.function.CharPredicate;

public enum Strings {
    ;
    public static String emptyIfNull(@Nullable String s) {
        return s != null ? s : "";
    }

    public static int indexOfNonSpace(char[] chars) {
        if (chars.length == 0 || !Character.isWhitespace(chars[0])) {
            return 0;
        }

        int position = 0;
        do {
            ++position;
            if (position == chars.length) {
                return chars.length;
            }
        } while (Character.isWhitespace(chars[position]));

        return position;
    }

    public static String substring(char[] chars, int start) {
        return substring(chars, start, chars.length);
    }

    public static String substring(char[] chars, int start, int end) {
        int span = end - start;
        return new String(chars, start, span);
    }

    public static String stripNonDigits(String s) {
        return stripNonMatching(s, Character::isDigit);
    }

    public static String stripNonNumbers(String s) {
        return stripNonMatching(s, Chars::isNumberChar);
    }

    public static String stripSpaces(String s) {
        return stripMatching(s, Character::isWhitespace);
    }

    private static String stripMatching(String s, CharPredicate filterNot) {
        return stripNonMatching(s, ch -> !filterNot.test(ch));
    }

    private static String stripNonMatching(String s, CharPredicate filter) {
        char[] chars = s.toCharArray();
        int position = 0;

        for (char ch : chars) {
            if (filter.test(ch)) {
                chars[position] = ch;
                ++position;
            }
        }

        return new String(chars, 0, position);
    }

    public static boolean isOneToNDigits(CharSequence text, int n) {
        int length = text.length();
        if (length < 1 || n < length) {
            return false;
        }

        for (int i = 0; i < length; ++i) {
            if (!Character.isDigit(text.charAt(i))) {
                return false;
            }
        }

        return true;
    }
}
