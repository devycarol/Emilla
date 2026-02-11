package net.emilla.lang;

import androidx.annotation.Nullable;

import net.emilla.grammar.TextStream;
import net.emilla.random.Dice;
import net.emilla.random.DiceRoller;

enum EnglishUnitedStates {;
    @Nullable
    public static DiceRoller diceRoller(String roll) {
        var stream = new TextStream(roll);
        Dice dice = diceFrom(stream, false);
        if (dice == null) {
            return null;
        }

        var roller = new DiceRoller();
        roller.add(dice);

        while (stream.hasRemaining()) {
            boolean minus = stream.skip('-');
            if (minus || stream.skip('+')) {
                dice = diceFrom(stream, minus);
                if (dice == null) {
                    return null;
                }
                roller.add(dice);
            } else {
                return null;
            }
        }

        return roller;
    }

    @Nullable
    private static Dice diceFrom(TextStream stream, boolean minus) {
        TextStream.Bookmark start = stream.position();

        Integer rollCount = stream.integer();
        if (rollCount == null) {
            return null;
        }

        if (!stream.skip('d')) {
            return Dice.modifier(rollCount);
        }

        Integer faceCount = stream.integer();
        if (faceCount == null || faceCount <= 0) {
            stream.reset(start);
            return null;
        }

        return new Dice(minus ? -rollCount : rollCount, faceCount);
    }
}
