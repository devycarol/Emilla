package net.emilla.lang.grammar.impl;

import net.emilla.lang.grammar.ListPhrase;

import java.util.regex.Pattern;

public final class ListPhraseEN_US implements ListPhrase {

    private static final String CONJUNCTION = "(?i)(?<=[^\\s,])\\s+(and|&)\\s+(?=[^\\s,])";
    private static final String COORDINATION = "(?<=[^\\s,]),[\\s,]*(?=[^\\s,])";
    private static final String SERIAL = "(?i)(?<=\\S),[\\s,]*(and|&)\\s+(?=[^\\s,])";

    private final String[] mItems;

    public ListPhraseEN_US(String phrase) {
        // todo: this may incorrectly destroy tokens like "red, and blue" -> "red" "blue" instead of
        //  "red" "and blue". the approach doesn't permit various list interpretations such as
        //  "items with commas", but it will suffice for now.
        // don't put commas in your contact names u jackal :P

        var coordination = Pattern.compile(COORDINATION);
        if (coordination.matcher(phrase).find()) {
            mItems = phrase.split(SERIAL + '|' + COORDINATION);
            return;
        }

        var conjunction = Pattern.compile(CONJUNCTION);
        if (conjunction.matcher(phrase).find()) {
            mItems = conjunction.split(phrase);
            return;
        }

        mItems = new String[]{phrase};
    }

    @Override
    public String[] items() {
        return mItems;
    }
}
