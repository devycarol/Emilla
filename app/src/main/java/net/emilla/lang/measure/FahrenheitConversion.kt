package net.emilla.lang.measure

data class FahrenheitConversion(@JvmField val degrees: Double, @JvmField val fromKelvin: Boolean) {

    fun convert(): Double = if (fromKelvin) degrees - 459.67 else degrees * 9.0 / 5.0 + 32.0
}
