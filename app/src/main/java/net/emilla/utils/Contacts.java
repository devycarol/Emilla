package net.emilla.utils;

import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;

public class Contacts {
private static final String
    DFLT_PHONES = "Cameron, (208) 555-1234\n" +
        "John, Johnny, (208) 555-6789\n" +
        "Susan cell, (970) 123-4567\n" +
        "Susan home, (970) 345-6789",
    DFLT_EMAILS = "Devy, devydev@example.com\n" +
        "Bugs, bugs@emilla.net";

private static HashMap<String, String> mapContacts(final String csv) {
    final HashMap<String, String> contactMap = new HashMap<>();
    for (final String line : csv.split("\\s*\n\\s*")) {
        final String[] vals = line.split("\\s*,\\s*");
        final int lastIdx = vals.length - 1;
        for (int i = 0; i < lastIdx; ++i)
        if (contactMap.put(vals[i].toLowerCase(), vals[lastIdx]) != null) Log.d("Emilla Contacts", "Duplicate contact discarded. Sorry!"); // TODO
    }
    return contactMap;
}

public static HashMap<String, String> mapPhones(final SharedPreferences prefs) {
    return mapContacts(prefs.getString("phones", DFLT_PHONES).trim());
}

public static HashMap<String, String> mapEmails(final SharedPreferences prefs) {
    return mapContacts(prefs.getString("emails", DFLT_EMAILS).trim());
}

public static String fromName(final String name, final HashMap<String, String> contactMap) {
    // TODO: need to warn about phonewords when using 'call' and no contact has been found
    // When you implement this for system contacts, you'll need to allow for distinguishing between "home", "mobile", etc.
    // when you do, make sure you allow for spaces in contact names and all that delightfully overcomplicated stuffstuff.
    // even harder: commas/ampersands in contact names, splitting by 'and' while still allowing that word in contact names
    final String get = contactMap.get(name.toLowerCase());
    return get == null ? name : get;
    // if a word is provided with no matching contact, it's returned unedited. this is a phoneword hazard ^
}

/**
 * @param names comma-separated list of names and/or phone numbers
 * @return comma-separated list of phone numbers (or just the one if only one was provided)
 */
public static String namesToPhones(final String names, final HashMap<String, String> phoneMap) {
    // test if is valid phone number: if ya, carry on; if na, search contacts for that string.
    // pull up best match, prompt w/ selection dialog if there are multiple good results
    if (names.contains(",") || names.contains("&")) {
        final StringBuilder sb = new StringBuilder();
        final String[] people = names.split(" *[,&] *");
        for (final String name : people) sb.append(fromName(name, phoneMap)).append(", ");
        sb.setLength(sb.length() - 2);
        return sb.toString();
    } else return fromName(names, phoneMap);
}

public static String[] namesToEmails(final String names, final HashMap<String, String> emailMap) {
    final String[] emails = names.replaceAll(",(\\s*,)+", ",").split("\\s*[,&]\\s*");
    int i = -1;
    for (final String name : emails) emails[++i] = fromName(name, emailMap);
    return emails;
}
}
