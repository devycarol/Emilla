package net.emilla.util;

public final class Chars {

    public static boolean isNonLineSpace(char ch) {
        return switch (ch) {
            case '\n', '\r' -> false;
            default -> Character.isWhitespace(ch);
        };
    }

    public static boolean differentLetters(char a, char b) {
        return compareIgnoreCase(a, b) != 0;
    }

    public static int compareIgnoreCase(char a, char b) {
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

    private Chars() {}

}
