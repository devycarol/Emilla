package net.emilla.measure;

import androidx.annotation.Nullable;
import androidx.annotation.PluralsRes;

import net.emilla.R;
import net.emilla.exception.UnreachableError;

import java.math.BigDecimal;
import java.math.MathContext;

public enum CustomaryUnit implements MeasureUnit {
    INCH(Customary.INCHES_IN_INCH, Measurement.LENGTH, R.plurals.inch),
    FOOT(Customary.INCHES_IN_FOOT, Measurement.LENGTH, R.plurals.foot),
    YARD(Customary.INCHES_IN_YARD, Measurement.LENGTH, R.plurals.yard),
    MILE(Customary.INCHES_IN_MILE, Measurement.LENGTH, R.plurals.mile),

    TEASPOON(Customary.TEASPOONS_IN_TEASPOON, Measurement.VOLUME, R.plurals.teaspoon),
    TABLESPOON(Customary.TEASPOONS_IN_TABLESPOON, Measurement.VOLUME, R.plurals.tablespoon),
    FLUID_OUNCE(Customary.TEASPOONS_IN_FLUID_OUNCE, Measurement.VOLUME, R.plurals.fluid_ounce),
    CUP(Customary.TEASPOONS_IN_CUP, Measurement.VOLUME, R.plurals.cup),
    PINT(Customary.TEASPOONS_IN_PINT, Measurement.VOLUME, R.plurals.pint),
    QUART(Customary.TEASPOONS_IN_QUART, Measurement.VOLUME, R.plurals.quart),
    GALLON(Customary.TEASPOONS_IN_GALLON, Measurement.VOLUME, R.plurals.gallon),

    OUNCE(Customary.OUNCES_IN_OUNCE, Measurement.WEIGHT, R.plurals.ounce),
    POUND(Customary.OUNCES_IN_POUND, Measurement.WEIGHT, R.plurals.pound),
    TON(Customary.OUNCES_IN_TON, Measurement.WEIGHT, R.plurals.ton),
;
    public static final BigDecimal YARDS_TO_METERS = new BigDecimal("0.9144");
    public static final BigDecimal GALLONS_TO_LITERS = new BigDecimal("3.785412");
    public static final BigDecimal POUNDS_TO_GRAMS = new BigDecimal("453.5924");

    private final int mScale;
    private final Measurement mMeasurement;
    @PluralsRes
    private final int mPlural;

    CustomaryUnit(int scale, Measurement measurement, @PluralsRes int plural) {
        mScale = scale;
        mMeasurement = measurement;
        mPlural = plural;
    }

    @Override
    public final Measurement measurement() {
        return mMeasurement;
    }

    @Override @Nullable
    public final BigDecimal convert(BigDecimal value, MeasureUnit toUnit) {
        if (toUnit.measurement() != mMeasurement) {
            return null;
        }

        return switch (toUnit) {
            case MetricUnit metricUnit -> toMetric(value, metricUnit.scale());
            case CustomaryUnit customaryUnit -> toCustomary(value, customaryUnit);
            case TemperatureUnit __ -> throw new UnreachableError();
        };
    }

    private BigDecimal toMetric(BigDecimal value, MetricScale scale) {
        return MetricScale.BASE.convert(
            switch (mMeasurement) {
                case LENGTH -> toCustomary(value, YARD).multiply(YARDS_TO_METERS);
                case VOLUME -> toCustomary(value, GALLON).multiply(GALLONS_TO_LITERS);
                case WEIGHT -> toCustomary(value, POUND).multiply(POUNDS_TO_GRAMS);
                case TEMPERATURE -> throw new UnreachableError();
            },
            scale
        );
    }

    public final BigDecimal toCustomary(BigDecimal value, CustomaryUnit unit) {
        int toScale = unit.mScale;
        if (mScale > toScale) {
            return value.multiply(BigDecimal.valueOf(mScale / toScale));
        }

        if (mScale < toScale) {
            return value.divide(BigDecimal.valueOf(toScale / mScale), MathContext.DECIMAL128);
        }

        return value;
    }

    @Override @PluralsRes
    public final int plural() {
        return mPlural;
    }
}
