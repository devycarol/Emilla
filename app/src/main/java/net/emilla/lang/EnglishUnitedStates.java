package net.emilla.lang;

import android.content.Context;

import androidx.annotation.Nullable;

import net.emilla.grammar.TextStream;
import net.emilla.measure.CelsiusConversion;
import net.emilla.measure.ConversionRequest;
import net.emilla.measure.FahrenheitConversion;
import net.emilla.measure.MeasureUnit;
import net.emilla.random.Dice;
import net.emilla.random.DiceRoller;
import net.emilla.time.ClockUnit;
import net.emilla.time.Meridiem;
import net.emilla.time.WallTime;
import net.emilla.util.DoubleFloat;
import net.emilla.util.Int;

import java.math.BigDecimal;
import java.util.EnumSet;

enum EnglishUnitedStates {;
    @Nullable
    public static WallTime wallTime(Context ctx, String s) {
        return TextStream.extract(s, stream -> wallTime(ctx, stream));
    }

    @Nullable
    private static WallTime wallTime(Context ctx, TextStream stream) {
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
        }}

        Meridiem meridiem;
        String meridiemChars = stream.token(EnglishUnitedStates::isMeridiemCharacter);
        if (meridiemChars != null) {
            switch (meridiemChars.toUpperCase()) {
            case "AM", "A" -> meridiem = Meridiem.AM;
            case "PM", "P" -> meridiem = Meridiem.PM;
            default -> {
                return null;
            }}
        } else {
            meridiem = null;
        }

        return WallTime.of(ctx, hour, minute, meridiem);
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
    public static Int durationSeconds(Context ctx, String s) {
        return TextStream.extractFirst(
            s,
            stream -> untilDuration(ctx, stream),
            EnglishUnitedStates::unitDuration,
            EnglishUnitedStates::clockDuration
        );
    }

    @Nullable
    private static Int untilDuration(Context ctx, TextStream stream) {
        if (!stream.skipFirst("until", "till", "til", "to") || !stream.isAtWordStart()) {
            return null;
        }

        WallTime wallTime = wallTime(ctx, stream);
        if (wallTime == null) {
            return null;
        }

        return new Int(wallTime.secondsToNextOccurrence());
    }

    @Nullable
    private static Int unitDuration(TextStream stream) {
        double seconds = 0.0;
        var used = EnumSet.<ClockUnit>noneOf(ClockUnit.class);

        int i = 0;
        do {
            DoubleFloat box = stream.unsignedDouble();
            if (box == null) {
                return null;
            }

            ClockUnit unit = clockUnit(used, stream);
            if (unit == null) {
                return null;
            }

            seconds += unit.toSeconds(box.doubleValue());
            ++i;
        } while (i < 3 && stream.hasRemaining());

        if (stream.hasRemaining()) {
            return null;
        }

        return Int.floor(seconds);
    }

    @Nullable
    private static ClockUnit clockUnit(EnumSet<ClockUnit> used, TextStream stream) {
        if (stream.skipFirst("hours", "hour") || stream.skip('h')) {
            return used.add(ClockUnit.HOUR)
                ? ClockUnit.HOUR
                : null
            ;
        }

        if (stream.skipFirst("minutes", "minute", "min") || stream.skip('m')) {
            return used.add(ClockUnit.MINUTE)
                ? ClockUnit.MINUTE
                : null
            ;
        }

        if (stream.skipFirst("seconds", "second", "secs", "sec") || stream.skip('s')) {
            return used.add(ClockUnit.SECOND)
                ? ClockUnit.SECOND
                : null
            ;
        }

        return null;
    }

    @Nullable
    private static Int clockDuration(TextStream stream) {
        Int box = stream.unsignedInt();
        if (box == null) {
            return null;
        }

        int value = box.intValue();
        if (stream.isFinished()) {
            return new Int(defaultUnit().toSeconds(value));
        }

        for (int i = 0; i < 2 && stream.skip(':'); ++i) {
            box = stream.unsignedInt();
            if (box == null) {
                return null;
            }

            value *= 60;
            value += box.intValue();
        }

        if (stream.hasRemaining()) {
            return null;
        }

        return new Int(value);
    }

    private static ClockUnit defaultUnit() {
        // todo config
        return ClockUnit.MINUTE;
    }

    @Nullable
    public static ConversionRequest unitConversion(String units) {
        return TextStream.extract(units, stream -> {
            BigDecimal value = stream.bigDecimal();
            if (value == null) {
                return null;
            }

            MeasureUnit from = measureUnit(stream);
            if (from == null || !stream.isAtWordStart()) {
                return null;
            }

            if (!(stream.skip("to") && stream.isAtWordStart())) {
                return null;
            }

            MeasureUnit to = measureUnit(stream);
            if (to == null) {
                return null;
            }

            return new ConversionRequest(from, value, to);
        });
    }

    @Nullable
    private static MeasureUnit measureUnit(TextStream stream) {
        // TODO
        return null;
    }

    @Nullable
    public static CelsiusConversion toCelsius(String temperature) {
        return TextStream.extract(temperature, stream -> {
            DoubleFloat box = stream.doubleFloat();
            if (box == null) {
                return null;
            }

            double degrees = box.doubleValue();
            if (stream.isFinished()) {
                return new CelsiusConversion(degrees, false);
            }

            boolean __ = stream.skip("degrees") || stream.skip('°');
            boolean wasFromKelvin = stream.skip("kelvin") || stream.skip('k');
            __ = wasFromKelvin || stream.skip("fahrenheit") || stream.skip('f');

            return new CelsiusConversion(degrees, wasFromKelvin);
        });
    }

    @Nullable
    public static FahrenheitConversion toFahrenheit(String temperature) {
        return TextStream.extract(temperature, stream -> {
            DoubleFloat box = stream.doubleFloat();
            if (box == null) {
                return null;
            }

            double degrees = box.doubleValue();
            if (stream.isFinished()) {
                return new FahrenheitConversion(degrees, false);
            }

            boolean __ = stream.skip("degrees") || stream.skip('°');
            boolean wasFromKelvin = stream.skip("kelvin") || stream.skip('k');
            __ = wasFromKelvin || stream.skip("celsius") || stream.skip('c');

            return new FahrenheitConversion(degrees, wasFromKelvin);
        });
    }

    @Nullable
    public static DiceRoller diceRoller(String roll) {
        return TextStream.extract(roll, stream -> {
            boolean isNegative = stream.skip('-');
            Dice dice = diceFrom(stream, isNegative);
            if (dice == null) {
                return null;
            }

            var roller = new DiceRoller();
            roller.add(dice);

            while (stream.hasRemaining()) {
                isNegative = stream.skip('-');
                if (isNegative || stream.skip('+')) {
                    dice = diceFrom(stream, isNegative);
                    if (dice == null) {
                        return null;
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
    private static Dice diceFrom(TextStream stream, boolean isNegative) {
        Int box = stream.integer();
        if (box == null) {
            return null;
        }

        int rollCount = box.intValue();
        if (!stream.skip('d')) {
            return Dice.modifier(rollCount);
        }

        box = stream.unsignedInt();
        if (box == null) {
            return null;
        }

        int faceCount = box.intValue();
        if (faceCount == 0) {
            return null;
        }

        if (isNegative) {
            rollCount = -rollCount;
        }

        return new Dice(rollCount, faceCount);
    }
}
