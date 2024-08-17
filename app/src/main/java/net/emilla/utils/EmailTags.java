package net.emilla.utils;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

import android.content.Intent;

import net.emilla.exceptions.EmlaBadCommandException;

import java.util.HashMap;
import java.util.regex.Matcher;

public class EmailTags { // Todo lang
public static final String CC = "(^| )cc";
public static final String BCC = "(^| )bcc";
public static final String TO = "(^| )to";
public static final String EMAIL_TAGS = "((" + CC + ")|(" + BCC + ")|(" + TO + "))";

public static boolean itHas(final String text, final String tagRgx) {
    Matcher m = compile(tagRgx + "\\s+\\S", CASE_INSENSITIVE).matcher(text); // must be trimmed
    if (m.find()) {
        if (m.find()) throw new EmlaBadCommandException("You can't have duplicate tags.");
        return true;
    }
    return false;
}

public static String getFrom(final String text, final String tagRgx, final String otherTags) {
    final Matcher m = compile(tagRgx + "\\s+", CASE_INSENSITIVE).matcher(text);
    m.find();
    int begin = m.end();
    m.usePattern(compile("$|\\s*" + otherTags)).find(begin);
    return text.substring(begin, m.start());
}

public static String strip(final String text, final String tagRgx, final String tag) {
    return compile(tagRgx + "\\s+" + tag + "\\s*", CASE_INSENSITIVE).matcher(text).replaceFirst("");
}

public static String putEmailsIfPresent(final String text, final String tagKey, final Intent intent,
        final String extra, final String tagSet, final HashMap<String, String> emailMap) {
    if (EmailTags.itHas(text, tagKey)) {
        final String tagVal = EmailTags.getFrom(text, tagKey, tagSet);
        intent.putExtra(extra, Contact.namesToEmails(tagVal, emailMap));
        return EmailTags.strip(text, tagKey, tagVal);
    }
    return text;
}
}
