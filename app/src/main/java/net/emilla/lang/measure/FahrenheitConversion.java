package net.emilla.lang.measure;

public record FahrenheitConversion(double degrees, boolean fromKelvin) {

    public double convert() {
        return degrees * 9.0 / 5.0 + (fromKelvin ? -459.67 : 32.0);
    }

}
