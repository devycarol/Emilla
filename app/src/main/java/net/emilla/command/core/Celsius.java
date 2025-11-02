package net.emilla.command.core;

import android.view.inputmethod.EditorInfo;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.lang.Lang;
import net.emilla.lang.measure.CelsiusConversion;
import net.emilla.math.Maths;

/*internal*/ final class Celsius extends CoreCommand {

    public static final String ENTRY = "celsius";

    public static boolean possible() {
        return true;
    }

    /*internal*/ Celsius(AssistActivity act) {
        super(act, CoreEntry.CELSIUS, EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected void run() {
        offer(act -> {});
    }

    @Override
    protected void run(String temperature) {
        CelsiusConversion celsius = Lang.celsius(temperature, CoreEntry.CELSIUS.name);

        String oldDegrees = Maths.prettyNumber(celsius.degrees);
        String unit = str(celsius.fromKelvin ? R.string.kelvin : R.string.fahrenheit);
        String celsiusDegrees = Maths.prettyNumber(celsius.convert());
        giveText(str(R.string.celsius_conversion, oldDegrees, unit, celsiusDegrees));
    }

}
