package net.emilla.util;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

import android.content.Intent;

import net.emilla.R;
import net.emilla.exception.EmlaBadCommandException;

import java.util.HashMap;
import java.util.regex.Matcher;

@Deprecated
public final class EmailTags { // Todo lang

    public static final String CC = "(^| )cc";
    public static final String BCC = "(^| )bcc";
    public static final String TO = "(^| )to";
    public static final String EMAIL_TAGS = "((" + CC + ")|(" + BCC + ")|(" + TO + "))";

    public static boolean itHas(String text, String tagRgx) {
        Matcher m = compile(tagRgx + "\\s+\\S", CASE_INSENSITIVE).matcher(text); // must be trimmed
        if (m.find()) {
            if (m.find()) throw new EmlaBadCommandException(R.string.command_email, R.string.error_duplicate_tags);
            return true;
        }
        return false;
    }

    public static String getFrom(String text, String tagRgx, String otherTags) {
        Matcher m = compile(tagRgx + "\\s+", CASE_INSENSITIVE).matcher(text);
        m.find();
        int begin = m.end();
        m.usePattern(compile("$|\\s*" + otherTags)).find(begin);
        return text.substring(begin, m.start());
    }

    public static String strip(String text, String tagRgx, String tag) {
        return compile(tagRgx + "\\s+" + tag + "\\s*", CASE_INSENSITIVE).matcher(text).replaceFirst("");
    }

    public static String putEmailsIfPresent(String text, String tagKey, Intent intent,
            String extra, String tagSet, HashMap<String, String> emailMap) {
        if (EmailTags.itHas(text, tagKey)) {
            String tagVal = EmailTags.getFrom(text, tagKey, tagSet);
            intent.putExtra(extra, Contacts.namesToEmails(tagVal, emailMap));
            return EmailTags.strip(text, tagKey, tagVal);
        }
        return text;
    }

    private EmailTags() {}
}
