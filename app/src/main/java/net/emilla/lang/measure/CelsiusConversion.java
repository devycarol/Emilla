package net.emilla.lang.measure;

public record CelsiusConversion(double degrees, boolean fromKelvin) {

    public double convert() {
        return fromKelvin
            ? degrees - 273.15
            : (degrees - 32.0) * 5.0 / 9.0;
    }

}
