package net.emilla.measure;

import java.math.BigDecimal;

public record ConversionRequest(MeasureUnit from, BigDecimal value, MeasureUnit to) {
}
