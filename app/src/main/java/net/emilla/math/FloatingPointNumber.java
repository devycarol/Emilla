package net.emilla.math;

final class FloatingPointNumber implements CalcValue<Double>, ArithmeticToken {
    private final double mValue;

    FloatingPointNumber(double value) {
        mValue = value;
    }

    @Override
    public Double get() {
        return mValue;
    }
}
