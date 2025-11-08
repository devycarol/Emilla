package net.emilla.lang.phrase.impl;

import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.exception.EmillaException;
import net.emilla.lang.phrase.RandRange;

import java.util.Scanner;

public final class RandRangeEN_US {

    public static RandRange instance(String range, @StringRes int errorTitle) {
        if (range.matches("[(\\[]-?\\d+,\\s*-?\\d+[)\\]]")) {
            // range notation.
            boolean leftInclus = range.charAt(0) == '[';
            int last = range.length() - 1;
            boolean rightInclus = range.charAt(last) == ']';
            range = range.substring(1, last);

            String[] split = range.split(",\\s*");

            int inclusStart = Integer.parseInt(split[0]);

            int exclusEnd = Integer.parseInt(split[1]);
            if (inclusStart > exclusEnd) {
                int tmp1 = inclusStart;
                inclusStart = exclusEnd;
                exclusEnd = tmp1;

                boolean tmp2 = leftInclus;
                leftInclus = rightInclus;
                rightInclus = tmp2;
            }

            if (!leftInclus) ++inclusStart;
            if (rightInclus) ++exclusEnd;

            return new RandRange(inclusStart, exclusEnd);
        } else if (range.matches("-?\\d+((,\\s*|\\s+(and|&|to))?\\s+)-?\\d+")) {
            // grammatical notation.
            var scn = new Scanner(range);

            int inclusStart = scn.nextInt();
            int inclusEnd = scn.nextInt();

            return new RandRange(inclusStart, inclusEnd + 1);
        } else if (range.matches("-?\\d+")) {
            // simple number
            int inclusEnd = Integer.parseInt(range);
            return new RandRange(inclusEnd, errorTitle);
        } else throw new EmillaException(errorTitle, R.string.error_invalid_number_range);
    }

    private RandRangeEN_US() {}
}
