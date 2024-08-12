package net.emilla.utils;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

import android.content.Intent;

import net.emilla.exceptions.EmlaBadCommandException;

import java.util.HashMap;
import java.util.regex.Matcher;

public class Tags { // TODO LANG
    // SMS tags
    public static final String SUBJECT = "/sub(ject)?";
    public static final String BODY = "/(b(ody?)?|m(essage|sg)?|t(e?xt)?)";
    public static final String SMS_TAGS = makeTags(SUBJECT, new String[]{BODY});

    // Email tags
    public static final String CC = "(^| )cc";
    public static final String BCC = "(^| )bcc";
    public static final String TO = "(^| )to";
    public static final String EMAIL_TAGS = makeTags(CC, new String[]{BCC, TO});

    // Calendar tags
    public static final String LOCATION = "/loc(ation)?";
    public static final String GUESTS = "/(with|guests?|em(ail)?s?)";
    public static final String DETAILS = "/(d(esc(ription)?)?|(det(ails)?|deets))";
    public static final String URL = "/(url|link)";
//    public static final String REMINDER = "/(rem(ind(er)?)?|notify)";
//    public static final String REPEAT = "/rep(eat)?";
    public static final String AVAIL = "/(avail(ability)?|stat(us)?)";
    public static final String ACCESS = "/(vis(ibility)?|acc(ess)?)";
//    public static final String TIMEZONE = "/(timezone|tz)";
//    public static final String COLOR = "/color";
//    public static final String CALENDAR = "/cal(endar)?";
    public static final String CALENDAR_TAGS = makeTags(LOCATION, new String[]{GUESTS, DETAILS,
            URL, /*REMINDER, REPEAT,*/ AVAIL, ACCESS/*, TIMEZONE, COLOR, CALENDAR*/});

    private static String makeTags(final String firstTag, final String[] remTags) {
        final StringBuilder sb = new StringBuilder("((");
        sb.append(firstTag);
        for (final String tag : remTags) sb.append(")|(").append(tag);
        sb.append("))");
        return sb.toString();
    }

    // todo: these should maybe deal with StringBuilders rather than Strings

    public static boolean itHas(final String text, final String tagRgx) {
        Matcher m = compile(tagRgx + "\\s+\\S", CASE_INSENSITIVE).matcher(text); // must be trimmed
        if (m.find()) {
            if (m.find()) throw new EmlaBadCommandException("You can't have duplicate tags."); // todo: dialog instead of failure
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
        Matcher m = compile(tagRgx + "\\s+" + tag + "\\s*", CASE_INSENSITIVE).matcher(text);
        return m.replaceFirst("");
    }

    public static String putIfPresent(final String text, final String tagKey, final Intent intent,
            final String extra, final String tagSet) {
        // todo: refine logic in Tags and cleanup putDetails
        // perhaps use the slashtags to generate new edittext fields
        // could use an audio feedback (B↓G↑A?) and change the submit button icon and color to indicate it will create a new edittext
        // or it could be an above-button. either way some way to decline the slashtag will be required. might not need slashes in that case.
        if (Tags.itHas(text, tagKey)) {
            final String tagVal = Tags.getFrom(text, tagKey, tagSet);
            intent.putExtra(extra, tagVal);
            return Tags.strip(text, tagKey, tagVal);
        }
        return text;
    }

    public static String putEmailsIfPresent(final String text, final String tagKey, final Intent intent,
            final String extra, final String tagSet, final HashMap<String, String> emailMap) {
        // todo: refine logic in Tags and cleanup putDetails
        // perhaps use the slashtags to generate new edittext fields
        // could use an audio feedback (B↓G↑A?) and change the submit button icon and color to indicate it will create a new edittext
        // or it could be an above-button. either way some way to decline the slashtag will be required. might not need slashes in that case.
        if (Tags.itHas(text, tagKey)) {
            final String tagVal = Tags.getFrom(text, tagKey, tagSet);
            intent.putExtra(extra, Contacts.namesToEmails(tagVal, emailMap));
            return Tags.strip(text, tagKey, tagVal);
        }
        return text;
    }
}
