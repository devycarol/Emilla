package net.emilla.lang.measure

data class CelsiusConversion(@JvmField val degrees: Double, @JvmField val fromKelvin: Boolean) {

    fun convert() = if (fromKelvin) degrees - 273.15 else (degrees - 32.0) * 5.0 / 9.0
}
