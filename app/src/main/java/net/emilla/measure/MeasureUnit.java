package net.emilla.measure;

import androidx.annotation.Nullable;
import androidx.annotation.PluralsRes;

import java.math.BigDecimal;

public sealed interface MeasureUnit permits MetricUnit, CustomaryUnit, TemperatureUnit {
    Measurement measurement();
    @Nullable
    BigDecimal convert(BigDecimal value, MeasureUnit toUnit);
    @PluralsRes
    int plural();
}
