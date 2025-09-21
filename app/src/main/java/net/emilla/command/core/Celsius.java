package net.emilla.command.core;

import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.lang.Lang;
import net.emilla.lang.measure.CelsiusConversion;
import net.emilla.math.Maths;

public final class Celsius extends CoreCommand {

    public static final String ENTRY = "celsius";
    @StringRes
    public static final int NAME = R.string.command_celsius;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_celsius;

    public static Yielder yielder() {
        return new Yielder(true, Celsius::new, ENTRY, NAME, ALIASES);
    }

    public static boolean possible() {
        return true;
    }

    private Celsius(AssistActivity act) {
        super(act, NAME,
              R.string.instruction_temperature,
              R.drawable.ic_temperature,
              R.string.summary_celsius,
              R.string.manual_celsius,
              EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected void run() {
        offer(() -> {});
    }

    @Override
    protected void run(String temperature) {
        CelsiusConversion celsius = Lang.celsius(temperature, NAME);

        String oldDegrees = Maths.prettyNumber(celsius.degrees);
        String unit = str(celsius.fromKelvin ? R.string.kelvin : R.string.fahrenheit);
        String celsiusDegrees = Maths.prettyNumber(celsius.convert());
        giveText(str(R.string.celsius_conversion, oldDegrees, unit, celsiusDegrees));
    }
}
