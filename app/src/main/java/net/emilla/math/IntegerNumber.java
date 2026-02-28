package net.emilla.math;

final class IntegerNumber implements CalcValue<Long>, BitwiseToken {
    private final long mValue;

    IntegerNumber(long value) {
        mValue = value;
    }

    @Override
    public Long get() {
        return mValue;
    }
}
