package net.emilla.measure;

public record CelsiusConversion(double degrees, boolean wasFromKelvin) {
    public double convert() {
        return wasFromKelvin
            ? degrees - 273.15
            : (degrees - 32.0) * 5.0 / 9.0
        ;
    }
}
