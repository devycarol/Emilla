package net.emilla.measure;

import androidx.annotation.Nullable;
import androidx.annotation.PluralsRes;

import net.emilla.grammar.TextStream;
import net.emilla.lang.Lang;

import java.math.BigDecimal;

public sealed interface MeasureUnit permits MetricUnit, CustomaryUnit, TemperatureUnit {
    Measurement measurement();
    @Nullable
    BigDecimal convert(BigDecimal value, MeasureUnit toUnit);
    @PluralsRes
    int plural();

    @Nullable
    static MeasureUnit from(Lang lang, TextStream stream) {
        return switch (lang) {
            case EN_US -> EnglishUS.measureUnit(stream);
        };
    }
}
