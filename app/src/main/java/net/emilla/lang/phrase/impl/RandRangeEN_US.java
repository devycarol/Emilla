package net.emilla.lang.phrase.impl;

import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.exception.EmillaException;
import net.emilla.lang.phrase.RandRange;
import net.emilla.util.Patterns;

import java.util.Scanner;
import java.util.regex.Pattern;

public enum RandRangeEN_US {
    ;

    private static final Pattern NUMBER_RANGE = Pattern.compile("[(\\[]-?\\d+,\\s*-?\\d+[)\\]]");
    private static final Pattern VERBAL_RANGE = Pattern.compile(
        "-?\\d+((,\\s*|\\s+(and|&|to))?\\s+)-?\\d+",
        Pattern.CASE_INSENSITIVE
    );
    private static final Pattern NUMERIC_RANGE = Pattern.compile("-?\\d+");

    public static RandRange instance(String range, @StringRes int errorTitle) {
        if (NUMBER_RANGE.matcher(range).matches()) {
            // range notation.
            boolean leftInclus = range.charAt(0) == '[';
            int last = range.length() - 1;
            boolean rightInclus = range.charAt(last) == ']';
            range = range.substring(1, last);

            String[] split = Patterns.TRIMMING_CSV.split(range);

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
        }

        if (VERBAL_RANGE.matcher(range).matches()) {
            // grammatical notation.
            var scn = new Scanner(range);

            int inclusStart = scn.nextInt();
            int inclusEnd = scn.nextInt();

            return new RandRange(inclusStart, inclusEnd + 1);
        }

        if (NUMERIC_RANGE.matcher(range).matches()) {
            // simple number
            int inclusEnd = Integer.parseInt(range);
            return new RandRange(inclusEnd, errorTitle);
        }

        throw new EmillaException(errorTitle, R.string.error_invalid_number_range);
    }

}
