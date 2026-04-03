package net.emilla.measure;

import androidx.annotation.PluralsRes;

import net.emilla.R;
import net.emilla.exception.UnreachableError;

import java.math.BigDecimal;

enum MetricScale {
    PICO(-12, R.plurals.picometer, R.plurals.picoliter, R.plurals.picogram),
    NANO(-9, R.plurals.nanometer, R.plurals.nanoliter, R.plurals.nanogram),
    MICRO(-6, R.plurals.micrometer, R.plurals.microliter, R.plurals.microgram),
    MILLI(-3, R.plurals.millimeter, R.plurals.milliliter, R.plurals.milligram),
    CENTI(-2, R.plurals.centimeter, R.plurals.centiliter, R.plurals.centigram),
    DECI(-1, R.plurals.decimeter, R.plurals.deciliter, R.plurals.decigram),
    BASE(0, R.plurals.meter, R.plurals.liter, R.plurals.gram),
    DECA(1, R.plurals.decameter, R.plurals.decaliter, R.plurals.decagram),
    HECTO(2, R.plurals.hectometer, R.plurals.hectoliter, R.plurals.hectogram),
    KILO(3, R.plurals.kilometer, R.plurals.kiloliter, R.plurals.kilogram),
    MEGA(6, R.plurals.megameter, R.plurals.megaliter, R.plurals.megagram),
    GIGA(9, R.plurals.gigameter, R.plurals.gigaliter, R.plurals.gigagram),
    TERA(12, R.plurals.terameter, R.plurals.teraliter, R.plurals.teragram),
;
    private final int mPower;
    @PluralsRes
    private final int mMetersPlural;
    @PluralsRes
    private final int mLitersPlural;
    @PluralsRes
    private final int mGramsPlural;

    MetricScale(
        int power,
        @PluralsRes int metersPlural,
        @PluralsRes int litersPlural,
        @PluralsRes int gramsPlural
    ) {
        mPower = power;
        mMetersPlural = metersPlural;
        mLitersPlural = litersPlural;
        mGramsPlural = gramsPlural;
    }

    public final BigDecimal convert(BigDecimal value, MetricScale toScale) {
        return value.scaleByPowerOfTen(mPower - toScale.mPower);
    }

    @PluralsRes
    public final int plural(Measurement measurement) {
        return switch (measurement) {
            case LENGTH -> mMetersPlural;
            case VOLUME -> mLitersPlural;
            case WEIGHT -> mGramsPlural;
            case TEMPERATURE -> throw new UnreachableError();
        };
    }
}
