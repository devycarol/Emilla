package net.emilla.measure;

public record FahrenheitConversion(double degrees, boolean wasFromKelvin) {
    public double convert() {
        double offset = wasFromKelvin
            ? -459.67
            : 32.0
        ;
        return degrees * 9.0 / 5.0 + offset;
    }
}
