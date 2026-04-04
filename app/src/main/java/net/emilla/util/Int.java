package net.emilla.util;

/// A nullable wrapper for the primitive `int` type without auto-boxing hazards.
public record Int(int intValue) {
    public static Int floor(double d) {
        return new Int((int) d);
    }
}
