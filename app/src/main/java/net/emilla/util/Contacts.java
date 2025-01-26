package net.emilla.util;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.regex.Pattern;

public final class Contacts {

    private static final Pattern PHONE_NUMBERS = Pattern.compile("\\+?[0-9*#][0-9*#() \\-./,;]*");

    public static boolean isPhoneNumbers(String s) {
        return PHONE_NUMBERS.matcher(s).matches();
    }

    public static String phonewordsToNumbers(@NonNull String namesOrNumbers) {
        // Todo: change this function to "nicely formatted number" like (208) 555-1234.
        StringBuilder sb = new StringBuilder();
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

    private static final String
        DFLT_PHONES = "Cameron, (208) 555-1234\n" +
            "John, Johnny, (208) 555-6789\n" +
            "Susan cell, (970) 123-4567\n" +
            "Susan home, (970) 345-6789",
        DFLT_EMAILS = "Devy, devydev@example.com\n" +
            "Bugs, bugs@emilla.net";

    private static HashMap<String, String> sPhones;
    private static HashMap<String, String> sEmails;

    private static HashMap<String, String> mapContacts(String csv) {
        HashMap<String, String> contactMap = new HashMap<>();
        for (String line : csv.split("\\s*\n\\s*")) {
            String[] vals = line.split("\\s*,\\s*");
            int lastIdx = vals.length - 1;
            for (int i = 0; i < lastIdx; ++i) {
                if (contactMap.put(vals[i].toLowerCase(), vals[lastIdx]) != null) {
                    Log.d("Emilla Contacts", "Duplicate contact discarded. Sorry!"); // TODO
                }
            }
        }
        return contactMap;
    }

    public static HashMap<String, String> mapPhones(SharedPreferences prefs) {
        return sPhones == null ? sPhones = mapContacts(prefs.getString("phones", DFLT_PHONES).trim())
                : sPhones;
    }

    public static HashMap<String, String> mapEmails(SharedPreferences prefs) {
        return sEmails == null ? sEmails = mapContacts(prefs.getString("emails", DFLT_EMAILS).trim())
                : sEmails;
    }

    public static void clean() {
        sPhones = null;
        sEmails = null;
    }

    public static String fromName(String name, HashMap<String, String> contactMap) {
        // TODO: need to warn about phonewords when using 'call' and no contact has been found
        // When you implement this for system contacts, you'll need to allow for distinguishing between "home", "mobile", etc.
        // when you do, make sure you allow for spaces in contact names and all that delightfully overcomplicated stuffstuff.
        // even harder: commas/ampersands in contact names, splitting by 'and' while still allowing that word in contact names
        String get = contactMap.get(name.toLowerCase());
        return get == null ? name : get;
        // if a word is provided with no matching contact, it's returned unedited. this is a phoneword hazard ^
    }

    /**
     * @param names comma-separated list of names and/or phone numbers
     * @return comma-separated list of phone numbers (or just the one if only one was provided)
     */
    public static String namesToPhones(String names, HashMap<String, String> phoneMap) {
        // test if is valid phone number: if ya, carry on; if na, search contacts for that string.
        // pull up best match, prompt w/ selection dialog if there are multiple good results
        if (names.contains(",") || names.contains("&")) {
            StringBuilder sb = new StringBuilder();
            String[] people = names.split(" *[,&] *");
            for (String name : people) sb.append(fromName(name, phoneMap)).append(", ");
            sb.setLength(sb.length() - 2);
            return sb.toString();
        }
        return fromName(names, phoneMap);
    }

    public static String[] namesToEmails(String names, HashMap<String, String> emailMap) {
        String[] emails = names.replaceAll(",(\\s*,)+", ",").split("\\s*[,&]\\s*");
        int i = -1;
        for (String name : emails) emails[++i] = fromName(name, emailMap);
        return emails;
    }

    public static String phoneNumber(Uri contact, ContentResolver resolver) {
        Uri contentUri = Phone.CONTENT_URI;
        String[] projection = {Phone.NUMBER};
        int IDX_NUMBER = 0;
        String selection = Phone.CONTACT_ID + " = ?";
        String[] selectionArgs = {contact.getLastPathSegment()};
        try (Cursor cur = resolver.query(contentUri, projection, selection, selectionArgs, null)) {
            if (cur != null && cur.moveToFirst()) return cur.getString(IDX_NUMBER);
        }
        return null;
    }

    public static String emailAddress(Uri contact, ContentResolver resolver) {
        Uri contentUri = Email.CONTENT_URI;
        String[] projection = {Email.ADDRESS};
        int IDX_ADDRESS = 0;
        String selection = Email.CONTACT_ID + " = ?";
        String[] selectionArgs = {contact.getLastPathSegment()};
        try (Cursor cur = resolver.query(contentUri, projection, selection, selectionArgs, null)) {
            if (cur != null && cur.moveToFirst()) return cur.getString(IDX_ADDRESS);
        }
        return null;
    }

    private Contacts() {}
}
