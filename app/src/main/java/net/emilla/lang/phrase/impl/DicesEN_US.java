package net.emilla.lang.phrase.impl;

import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.exception.EmillaException;
import net.emilla.lang.phrase.Dice;
import net.emilla.lang.phrase.Dices;
import net.emilla.util.Strings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

public enum DicesEN_US {
    ;

    private static final Pattern DICE_ROLL = Pattern.compile(
        "((-\\d)?\\d*D\\d+|-?\\d+)([+-]((-\\d)?\\d*D\\d+|-?\\d+))*",
        Pattern.CASE_INSENSITIVE
    );

    public static Dices instance(String roll, @StringRes int errorTitle) {
        String actualRoll = Strings.stripSpaces(roll);
        if (!DICE_ROLL.matcher(actualRoll).matches()) {
            throw new EmillaException(errorTitle, R.string.error_invalid_dice_roll);
        }

        var dices = new ArrayList<Dice>(2);

        for (Dice dice : dices(actualRoll, errorTitle)) {
            int index = dices.indexOf(dice);
            if (index >= 0) {
                dices.get(index).add(dice.count());
            } else {
                dices.add(dice);
            }
        }

        return new Dices(dices.toArray(Dice[]::new));
    }

    private static Iterable<Dice> dices(String roll, @StringRes int errorTitle) {
        return () -> new Iterator<Dice>() {
            private boolean negative = roll.charAt(0) == '-';
            private int pos = negative ? 1 : 0;
            private final int length = roll.length();

            @Override
            public boolean hasNext() {
                return pos < length;
            }

            @Override
            public Dice next() {
                int count = nextCount();
                int faces;
                if (hasNext() && atD()) {
                    ++pos; // skip the 'D'.
                    faces = nextInt();
                    if (faces < 1) {
                        throw new EmillaException(errorTitle, R.string.error_invalid_dice_roll);
                    }
                } else {
                    faces = 1; // just adding `count` as a constant—"d1"
                }

                if (hasNext()) {
                    negative = roll.charAt(pos) == '-';
                    while (++pos < length) {
                        char c = roll.charAt(pos);
                        if (c == '-') negative = !negative;
                        else if (c != '+') break;
                    }
                }

                return new Dice(count, faces);
            }

            private int nextCount() {
                int count = atD() ? 1 : nextInt();
                return negative ? -count : count;
            }

            private boolean atD() {
                char c = roll.charAt(pos);
                return c == 'D' || c == 'd';
            }

            private int nextInt() {
                int n = 0;
                char c;
                while (hasNext() && Character.isDigit(c = roll.charAt(pos))) {
                    n = n * 10 + c - '0';
                    ++pos;
                }
                return n;
            }
        };
    }

}
