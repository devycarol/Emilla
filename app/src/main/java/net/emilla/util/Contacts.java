package net.emilla.util;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import androidx.annotation.NonNull;

import java.util.regex.Pattern;

public final class Contacts {

    private static final Pattern PHONE_NUMBERS = Pattern.compile("\\+?[0-9*#][0-9*#() \\-./,;]*");

    public static boolean isPhoneNumbers(String s) {
        return PHONE_NUMBERS.matcher(s).matches();
    }

    public static String phonewordsToNumbers(@NonNull String namesOrNumbers) {
        // Todo: change this function to "nicely formatted number" like (208) 555-1234.
        var sb = new StringBuilder();
        for (int i = 0; i < namesOrNumbers.length(); ++i) {
            char c = namesOrNumbers.charAt(i);
            if (isAcceptablePhoneChar(c)) sb.append(c);
            else {
                int letterDigit = keypadNumber(c);
                if (letterDigit != -1) sb.append(letterDigit);
                else if (sb.length() == 0 && c == '+') sb.append('+');
            }
        }
        return sb.toString();
    }

    private static boolean isAcceptablePhoneChar(char c) {
        return switch (c) {
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                 '#', '*', '(', ')', ' ', '-', '.', '/', ',', ';' -> true;
            default -> false;
        };
    }

    private static int keypadNumber(char letter) {
        if ('A' <= letter && letter <= 'Z') return letterDigit(letter);
        return letterDigit((char) (letter - 'a' + 'A'));
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

    public static String phoneNumber(Uri contact, ContentResolver cr) {
        Uri contentUri = Phone.CONTENT_URI;
        String[] projection = {Phone.NUMBER};
        int IDX_NUMBER = 0;
        var selection = Phone.CONTACT_ID + " = ?";
        String[] selectionArgs = {contact.getLastPathSegment()};
        try (Cursor cur = cr.query(contentUri, projection, selection, selectionArgs, null)) {
            if (cur != null && cur.moveToFirst()) return cur.getString(IDX_NUMBER);
        }
        return null;
    }

    public static String emailAddress(Uri contact, ContentResolver cr) {
        Uri contentUri = Email.CONTENT_URI;
        String[] projection = {Email.ADDRESS};
        int IDX_ADDRESS = 0;
        var selection = Email.CONTACT_ID + " = ?";
        String[] selectionArgs = {contact.getLastPathSegment()};
        try (Cursor cur = cr.query(contentUri, projection, selection, selectionArgs, null)) {
            if (cur != null && cur.moveToFirst()) return cur.getString(IDX_ADDRESS);
        }
        return null;
    }

    private Contacts() {}
}
