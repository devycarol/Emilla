package net.emilla.lang;

import androidx.annotation.Nullable;

import net.emilla.grammar.TextStream;
import net.emilla.random.Dice;
import net.emilla.random.DiceRoller;
import net.emilla.time.Meridiem;
import net.emilla.time.WallTime;

enum EnglishUnitedStates {;
    @Nullable
    public static WallTime wallTime(String s) {
        return TextStream.extract(s, stream -> {
            String time = stream.token(ch -> Character.isDigit(ch) || ch == ':');
            if (time == null) {
                return null;
            }

            int colonIndex = time.indexOf(':');
            if (colonIndex == 0) {
                return null;
            }

            int length = time.length();
            if (colonIndex > 0) {
                if (colonIndex != length - 3 || colonIndex != time.lastIndexOf(':')) {
                    return null;
                }

                time = time.substring(0, colonIndex) + time.substring(colonIndex + 1);
                --length;
            }

            int hour;
            int minute;
            switch (length) {
                case 1 -> {
                    hour = digitAt(time, 0);
                    minute = 0;
                }
                case 2 -> {
                    hour = digitAt(time, 0) * 10 + digitAt(time, 1);
                    minute = 0;
                }
                case 3 -> {
                    hour = digitAt(time, 0);
                    minute = digitAt(time, 1) * 10 + digitAt(time, 2);
                }
                case 4 -> {
                    hour = digitAt(time, 0) * 10 + digitAt(time, 1);
                    minute = digitAt(time, 2) * 10 + digitAt(time, 3);
                }
                default -> {
                    return null;
                }
            }

            Meridiem meridiem;
            String meridiemChars = stream.token(EnglishUnitedStates::isMeridiemCharacter);
            if (meridiemChars != null) {
                switch (meridiemChars.toUpperCase()) {
                    case "AM", "A" -> meridiem = Meridiem.AM;
                    case "PM", "P" -> meridiem = Meridiem.PM;
                    default -> {
                        return null;
                    }
                }
            } else {
                meridiem = null;
            }

            return WallTime.of(hour, minute, meridiem);
        });
    }

    private static int digitAt(String time, int index) {
        return Character.digit(time.charAt(index), 10);
    }

    private static boolean isMeridiemCharacter(char ch) {
        return switch (ch) {
            case 'A', 'a', 'P', 'p', 'M', 'm' -> true;
            default -> false;
        };
    }

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

        return new Dice(
            minus
                ? -rollCount
                : rollCount
            ,
            faceCount
        );
    }
}
