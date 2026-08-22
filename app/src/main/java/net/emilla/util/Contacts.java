package net.emilla.util;

import java.util.regex.Pattern;

public enum Contacts {;
    private static final Pattern PHONE_NUMBERS = Pattern.compile("\\+?[0-9*#][0-9*#() \\-./,;]*");

    public static boolean isPhoneNumbers(CharSequence text) {
        return PHONE_NUMBERS.matcher(text).matches();
    }

    public static String phonewordsToNumbers(String namesOrNumbers) {
        // Todo: change this function to "nicely formatted number" like (208) 555-1234.
        var sb = new StringBuilder();
        for (int i = 0; i < namesOrNumbers.length(); ++i) {
            char c = namesOrNumbers.charAt(i);
            if (isPhoneChar(c)) {
                sb.append(c);
            } else {
                int letterDigit = keypadNumber(c);
                if (letterDigit != -1) {
                    sb.append(letterDigit);
                } else if (sb.length() == 0 && c == '+') {
                    sb.append('+');
                }
            }
        }
        return sb.toString();
    }

    private static boolean isPhoneChar(char c) {
        return switch (c) {
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                 '#', '*', '(', ')', ' ', '-', '.', '/', ',', ';' -> true;
            default -> false;
        };
    }

    private static int keypadNumber(char letter) {
        char uppercaseLetter = 'A' <= letter && letter <= 'Z'
            ? letter
            : (char) (letter - 'a' + 'A')
        ;
        return letterDigit(uppercaseLetter);
    }

    private static int letterDigit(char uppercaseLetter) {
        return switch (uppercaseLetter) {
            case 'A', 'B', 'C' -> 2;
            case 'D', 'E', 'F' -> 3;
            case 'G', 'H', 'I' -> 4;
            case 'J', 'K', 'L' -> 5;
            case 'M', 'N', 'O' -> 6;
            case 'P', 'Q', 'R', 'S' -> 7;
            case 'T', 'U', 'V' -> 8;
            case 'W', 'X', 'Y', 'Z' -> 9;
            default -> -1;
        };
    }
}
