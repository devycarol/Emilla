package net.emilla.lang;

import android.content.Context;

import androidx.annotation.Nullable;

import net.emilla.grammar.TextStream;
import net.emilla.measure.ConversionRequest;
import net.emilla.measure.CustomaryUnit;
import net.emilla.measure.MeasureUnit;
import net.emilla.measure.Measurement;
import net.emilla.measure.MetricScale;
import net.emilla.measure.MetricUnit;
import net.emilla.measure.TemperatureUnit;
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
        if (stream.skipFirst("degrees", "degree") || stream.skip('°')) {
            if (stream.skip("fahrenheit") || stream.skip('f')) {
                return TemperatureUnit.FAHRENHEIT;
            }

            if (stream.skip("celsius") || stream.skip('f')) {
                return TemperatureUnit.CELSIUS;
            }

            if (stream.skip("kelvin") || stream.skip('k')) {
                return TemperatureUnit.KELVIN;
            }

            return null;
        }

        String word = stream.token(ch -> !Character.isWhitespace(ch));
        if (word == null) {
            return null;
        }

        String normalizedWord = Lang.normalize(word);
        return switch (normalizedWord) {
            // todo: metamorphic abbreviations. 'c' can be interpreted as cups if the other unit is
            //  volume and celsius if the other unit is temperature. likewise for "pt" if we add
            //  "points" vs. pints. just remember "c to c" is still ambiguous—don't infinitely loop!
            case "inches", "inch", "in.", "in" -> CustomaryUnit.INCH;
            case "feet", "foot", "ft.", "ft" -> CustomaryUnit.FOOT;
            case "yards", "yard", "yd.", "yd" -> CustomaryUnit.YARD;
            case "miles", "mile", "mi.", "mi" -> CustomaryUnit.MILE;

            case "microns", "micron" -> new MetricUnit(Measurement.LENGTH, MetricScale.MICRO);

            case "teaspoons", "teaspoon", "tsp.", "tsp" -> CustomaryUnit.TEASPOON;
            case "tablespoons", "tablespoon", "tbsp.", "tbsp" -> CustomaryUnit.TABLESPOON;
            case "fluid", "fl.", "fl" -> {
                if (!stream.skipFirst("ounces", "ounce", "oz.", "oz")) {
                    yield null;
                }

                yield CustomaryUnit.FLUID_OUNCE;
            }
            case "cups", "cup", "c." -> CustomaryUnit.CUP;
            case "pints", "pint", "pt.", "pt" -> CustomaryUnit.PINT;
            case "quarts", "quart", "qt.", "qt" -> CustomaryUnit.QUART;
            case "gallons", "gallon", "gal.", "gal" -> CustomaryUnit.GALLON;

            case "ounces", "ounce", "oz.", "oz" -> CustomaryUnit.OUNCE;
            case "pounds", "pound", "lbs.", "lbs", "lb.", "lb" -> CustomaryUnit.POUND;
            case "tonnes", "tons", "ton" -> CustomaryUnit.TON;

            case "fahrenheit", "f" -> TemperatureUnit.FAHRENHEIT;
            case "celsius", "c" -> TemperatureUnit.CELSIUS;
            case "kelvin", "k" -> TemperatureUnit.KELVIN;

            default -> {
                int length = normalizedWord.length();
                Measurement measurement;
                int suffixLength = lengthOfFirstSuffix(
                    normalizedWord, "metres", "metre", "meters", "meter", "m"
                );
                if (suffixLength >= 0) {
                    measurement = Measurement.LENGTH;
                } else {
                    suffixLength = lengthOfFirstSuffix(
                        normalizedWord, "litres", "litre", "liters", "liter", "l"
                    );
                    if (suffixLength >= 0) {
                        measurement = Measurement.VOLUME;
                    } else {
                        suffixLength = lengthOfFirstSuffix(
                            normalizedWord, "grammes", "gramme", "grams", "gram", "g"
                        );
                        if (suffixLength < 0) {
                            yield null;
                        }

                        measurement = Measurement.WEIGHT;
                    }
                }

                if (suffixLength == length) {
                    yield new MetricUnit(measurement, MetricScale.BASE);
                }

                var scale = suffixLength == 1
                    ? switch (normalizedWord.substring(0, length - 1)) {
                        case "p" -> MetricScale.PICO;
                        case "n" -> MetricScale.NANO;
                        case "μ" -> MetricScale.MICRO;
                        case "m" -> word.charAt(0) == 'M'
                            ? MetricScale.MEGA
                            : MetricScale.MILLI
                        ;
                        case "c" -> MetricScale.CENTI;
                        case "d" -> MetricScale.DECI;
                        case "da" -> MetricScale.DECA;
                        case "h" -> MetricScale.HECTO;
                        case "k" -> MetricScale.KILO;
                        case "g" -> MetricScale.GIGA;
                        case "t" -> MetricScale.TERA;
                        default -> null;
                    } : switch (normalizedWord.substring(0, length - suffixLength)) {
                        case "pico" -> MetricScale.PICO;
                        case "nano" -> MetricScale.NANO;
                        case "micro" -> MetricScale.MICRO;
                        case "milli" -> MetricScale.MILLI;
                        case "centi" -> MetricScale.CENTI;
                        case "deci" -> MetricScale.DECI;
                        case "deca" -> MetricScale.DECA;
                        case "hecto" -> MetricScale.HECTO;
                        case "kilo" -> MetricScale.KILO;
                        case "mega" -> MetricScale.MEGA;
                        case "giga" -> MetricScale.GIGA;
                        case "tera" -> MetricScale.TERA;
                        default -> null;
                    }
                ;
                if (scale == null) {
                    yield null;
                }

                yield new MetricUnit(measurement, scale);
            }
        };
    }

    private static int lengthOfFirstSuffix(String s, String... candidates) {
        for (String candidate : candidates) {
            if (s.endsWith(candidate)) {
                return candidate.length();
            }
        }

        return -1;
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
