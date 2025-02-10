package net.emilla.contact;

import androidx.annotation.NonNull;

import net.emilla.lang.Lang;

public record MultiSearch(String selection, String[] selectionArgs, boolean hasMultipleTerms) {

    public static MultiSearch instance(@NonNull String baseSelection, @NonNull String search) {
        String[] terms = Lang.list(search).items();

        final var selection = new StringBuilder(baseSelection);
        terms[0] = "%" + terms[0] + "%";
        for (int i = 1; i < terms.length; i++) {
            terms[i] = "%" + terms[i] + "%";
            selection.append(" OR ").append(baseSelection);
        }

        return new MultiSearch(selection.toString(), terms, terms.length > 1);
    }
}
