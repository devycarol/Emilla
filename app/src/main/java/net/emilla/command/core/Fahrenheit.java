package net.emilla.command.core;

import android.view.inputmethod.EditorInfo;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.lang.Lang;
import net.emilla.lang.measure.FahrenheitConversion;
import net.emilla.math.Maths;

public final class Fahrenheit extends CoreCommand {

    public static final String ENTRY = "fahrenheit";

    public static Yielder yielder() {
        return new Yielder(CoreEntry.FAHRENHEIT, true);
    }

    public static boolean possible() {
        return true;
    }

    /*internal*/ Fahrenheit(AssistActivity act) {
        super(act, CoreEntry.FAHRENHEIT, EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected void run() {
        offer(act -> {});
    }

    @Override
    protected void run(String temperature) {
        FahrenheitConversion fahrenheit = Lang.fahrenheit(temperature, CoreEntry.FAHRENHEIT.name);

        String oldDegrees = Maths.prettyNumber(fahrenheit.degrees);
        String unit = str(fahrenheit.fromKelvin ? R.string.kelvin : R.string.celsius);
        String fahrenheitDegrees = Maths.prettyNumber(fahrenheit.convert());
        giveText(str(R.string.fahrenheit_conversion, oldDegrees, unit, fahrenheitDegrees));
    }
}
