package net.emilla.math;

import java.util.function.Supplier;

sealed interface CalcValue<V> extends Supplier<V> permits FloatingPointNumber, IntegerNumber {
}
