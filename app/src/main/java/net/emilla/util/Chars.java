package net.emilla.util;

public enum Chars {
    ;

    public static boolean isLineSeparator(int ch) {
        return ch == '\n' || ch == '\r';
    }

    public static boolean isNonLineSpace(int ch) {
        return !isLineSeparator(ch) && Character.isWhitespace(ch);
    }

    public static boolean differentLetters(char a, char b) {
        return compareIgnoreCase(a, b) != 0;
    }

    private static int compareIgnoreCase(char a, char b) {
        if (a != b && Character.toUpperCase(a) != Character.toUpperCase(b)) {
            a = Character.toLowerCase(a);
            b = Character.toLowerCase(b);
            if (a != b) return a - b;
        }

        return 0;
    }

    public static boolean isSignOrDigit(char ch) {
        return isSign(ch) || Character.isDigit(ch);
    }

    public static boolean isSignOrNumberChar(char ch) {
        return isSign(ch) || isNumberChar(ch);
    }

    public static boolean isNumberChar(char ch) {
        return ch == '.' || Character.isDigit(ch);
    }

    private static boolean isSign(char ch) {
        return switch (ch) {
            case '+', '-' -> true;
            default -> false;
        };
    }

}
