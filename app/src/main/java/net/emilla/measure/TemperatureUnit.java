package net.emilla.measure;

import androidx.annotation.Nullable;
import androidx.annotation.PluralsRes;

import net.emilla.R;

import java.math.BigDecimal;
import java.math.MathContext;

public enum TemperatureUnit implements MeasureUnit {
    FAHRENHEIT(R.plurals.fahrenheit) {
        @Override
        BigDecimal toFahrenheit(BigDecimal value) {
            return value;
        }

        @Override
        BigDecimal toCelsius(BigDecimal value) {
            return value
                .subtract(THIRTY_TWO)
                .multiply(FIVE)
                .divide(NINE, MathContext.DECIMAL128)
            ;
        }

        @Override
        BigDecimal toKelvin(BigDecimal value) {
            return CELSIUS.toKelvin(toCelsius(value));
        }
    },
    CELSIUS(R.plurals.celsius) {
        @Override
        BigDecimal toFahrenheit(BigDecimal value) {
            return value
                .multiply(NINE)
                .divide(FIVE, MathContext.UNLIMITED)
                .add(THIRTY_TWO)
            ;
        }

        @Override
        BigDecimal toCelsius(BigDecimal value) {
            return value;
        }

        @Override
        BigDecimal toKelvin(BigDecimal value) {
            return value.add(C_K_DELTA);
        }
    },
    KELVIN(R.plurals.kelvin) {
        @Override
        BigDecimal toFahrenheit(BigDecimal value) {
            return CELSIUS.toFahrenheit(toCelsius(value));
        }

        @Override
        BigDecimal toCelsius(BigDecimal value) {
            return value.subtract(C_K_DELTA);
        }

        @Override
        BigDecimal toKelvin(BigDecimal value) {
            return value;
        }
    },
;
    private static final BigDecimal FIVE = BigDecimal.valueOf(5);
    private static final BigDecimal NINE = BigDecimal.valueOf(9);
    private static final BigDecimal THIRTY_TWO = BigDecimal.valueOf(32);
    private static final BigDecimal C_K_DELTA = new BigDecimal("273.15");

    @PluralsRes
    private final int mPlural;

    TemperatureUnit(@PluralsRes int plural) {
        mPlural = plural;
    }

    abstract BigDecimal toFahrenheit(BigDecimal value);
    abstract BigDecimal toCelsius(BigDecimal value);
    abstract BigDecimal toKelvin(BigDecimal value);

    @Override
    public final Measurement measurement() {
        return Measurement.TEMPERATURE;
    }

    @Override @Nullable
    public final BigDecimal convert(BigDecimal value, MeasureUnit toUnit) {
        if (!(toUnit instanceof TemperatureUnit temperatureUnit)) {
            return null;
        }

        return switch (temperatureUnit) {
            case FAHRENHEIT -> toFahrenheit(value);
            case CELSIUS -> toCelsius(value);
            case KELVIN -> toKelvin(value);
        };
    }

    @Override @PluralsRes
    public final int plural() {
        return mPlural;
    }
}
