package net.emilla.util;

public enum Chars {;
    public static boolean isLineSeparator(int ch) {
        return ch == '\n' || ch == '\r';
    }

    public static boolean isNonLineSpace(int ch) {
        return !isLineSeparator(ch) && Character.isWhitespace(ch);
    }

    public static boolean isNumberChar(char ch) {
        return ch == '.' || Character.isDigit(ch);
    }

    public static boolean isSign(char ch) {
        return switch (ch) {
            case '+', '-' -> true;
            default -> false;
        };
    }
}
