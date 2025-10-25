package net.emilla.lang.phrase.impl;

import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.exception.EmillaException;
import net.emilla.lang.phrase.Dice;
import net.emilla.lang.phrase.Dices;
import net.emilla.struct.sort.SortedArray;

import java.util.Iterator;

public final class DicesEN_US {

    public static Dices instance(String roll, @StringRes int errorTitle) {
        String actualRoll = roll.replaceAll("\\s+", "");
        if (!actualRoll.matches("(?i)((-\\d)?\\d*D\\d+|-?\\d+)([+-]((-\\d)?\\d*D\\d+|-?\\d+))*")) {
            throw new EmillaException(errorTitle, R.string.error_invalid_dice_roll);
        }

        var dices = new SortedArray<Dice>(1);

        for (Dice dice : dices(actualRoll, errorTitle)) {
            Dice retrieve = dices.retrieve(dice);
            if (retrieve == null) {
                dices.add(dice);
            } else {
                retrieve.add(dice.count());
            }
        }

        return new Dices(dices);
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

    private DicesEN_US() {}
}
