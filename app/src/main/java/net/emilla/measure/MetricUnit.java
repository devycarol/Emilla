package net.emilla.measure;

import androidx.annotation.Nullable;
import androidx.annotation.PluralsRes;

import net.emilla.exception.UnreachableError;

import java.math.BigDecimal;
import java.math.MathContext;

record MetricUnit(@Override Measurement measurement, MetricScale scale) implements MeasureUnit {
    @Override @Nullable
    public BigDecimal convert(BigDecimal value, MeasureUnit toUnit) {
        if (toUnit.measurement() != measurement) {
            return null;
        }

        return switch (toUnit) {
            case MetricUnit metricUnit -> scale.convert(value, metricUnit.scale);
            case CustomaryUnit customaryUnit -> toCustomary(value, customaryUnit);
            case TemperatureUnit __ -> throw new UnreachableError();
        };
    }

    private BigDecimal toCustomary(BigDecimal value, CustomaryUnit unit) {
        value = scale.convert(value, MetricScale.BASE);
        return switch (measurement) {
            case LENGTH -> CustomaryUnit.YARD.toCustomary(
                value.divide(CustomaryUnit.YARDS_TO_METERS, MathContext.DECIMAL128),
                unit
            );
            case VOLUME -> CustomaryUnit.GALLON.toCustomary(
                value.divide(CustomaryUnit.GALLONS_TO_LITERS, MathContext.DECIMAL128),
                unit
            );
            case WEIGHT -> CustomaryUnit.POUND.toCustomary(
                value.divide(CustomaryUnit.POUNDS_TO_GRAMS, MathContext.DECIMAL128),
                unit
            );
            case TEMPERATURE -> throw new UnreachableError();
        };
    }

    @Override @PluralsRes
    public int plural() {
        return scale.plural(measurement);
    }
}
