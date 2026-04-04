package net.emilla.measure;

import androidx.annotation.Nullable;

import net.emilla.grammar.TextStream;
import net.emilla.lang.Lang;

enum EnglishUS {;
    @Nullable
    public static MeasureUnit measureUnit(TextStream stream) {
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
}
