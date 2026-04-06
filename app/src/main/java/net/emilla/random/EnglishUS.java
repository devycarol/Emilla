package net.emilla.random;

import androidx.annotation.Nullable;

import net.emilla.lang.TextStream;
import net.emilla.util.Int;

enum EnglishUS {;
    @Nullable
    public static DiceRoller diceRoller(String roll) {
        return TextStream.extract(roll, stream -> {
            boolean isNegative = stream.skip('-');
            Dice dice = dice(stream);
            if (dice == null) {
                return null;
            }

            if (isNegative) {
                dice = dice.negate();
            }

            var roller = new DiceRoller();
            roller.add(dice);

            while (stream.hasRemaining()) {
                isNegative = stream.skip('-');
                if (isNegative || stream.skip('+')) {
                    dice = dice(stream);
                    if (dice == null) {
                        return null;
                    }

                    if (isNegative) {
                        dice = dice.negate();
                    }
                    roller.add(dice);
                } else {
                    return null;
                }
            }

            return roller;
        });
    }

    @Nullable
    private static Dice dice(TextStream stream) {
        Int box = stream.integer();
        int rollCount;
        if (box != null) {
            rollCount = box.intValue();
            if (!stream.skip('d')) {
                return Dice.modifier(rollCount);
            }
        } else {
            if (!stream.skip('d')) {
                return null;
            }

            rollCount = 1;
        }

        box = stream.unsignedInt();
        if (box == null) {
            return null;
        }

        int faceCount = box.intValue();
        if (faceCount == 0) {
            return null;
        }

        return new Dice(rollCount, faceCount);
    }
}
