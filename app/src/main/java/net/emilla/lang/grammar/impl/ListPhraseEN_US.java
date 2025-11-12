package net.emilla.lang.grammar.impl;

import net.emilla.lang.grammar.ListPhrase;

import java.util.regex.Pattern;

public final class ListPhraseEN_US implements ListPhrase {

    private static final Pattern CONJUNCTION = Pattern.compile("(?<=[^\\s,])\\s+(and|&)\\s+(?=[^\\s,])", Pattern.CASE_INSENSITIVE);
    private static final Pattern COORDINATION = Pattern.compile("(?<=[^\\s,]),[\\s,]*(?=[^\\s,])");
    private static final Pattern SERIAL = Pattern.compile("(?<=\\S),[\\s,]*(and|&)\\s+(?=[^\\s,])", Pattern.CASE_INSENSITIVE);
    private static final Pattern SEPARATION = Pattern.compile(SERIAL + "|" + COORDINATION, Pattern.CASE_INSENSITIVE);

    private final String[] mItems;

    public ListPhraseEN_US(String phrase) {
        // todo: this may incorrectly destroy tokens like "red, and blue" -> "red" "blue" instead of
        //  "red" "and blue". the approach doesn't permit various list interpretations such as
        //  "items with commas", but it will suffice for now.
        // don't put commas in your contact names u jackal :P

        if (COORDINATION.matcher(phrase).find()) {
            mItems = SEPARATION.split(phrase);
            return;
        }

        if (CONJUNCTION.matcher(phrase).find()) {
            mItems = CONJUNCTION.split(phrase);
            return;
        }

        mItems = new String[]{phrase};
    }

    @Override
    public String[] items() {
        return mItems;
    }
}
