package net.emilla.command.core;

import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.lang.Lang;
import net.emilla.lang.measure.FahrenheitConversion;
import net.emilla.math.Maths;

public final class Fahrenheit extends CoreCommand {

    public static final String ENTRY = "fahrenheit";
    @StringRes
    public static final int NAME = R.string.command_fahrenheit;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_fahrenheit;

    public static Yielder yielder() {
        return new Yielder(true, Fahrenheit::new, ENTRY, NAME, ALIASES);
    }

    public static boolean possible() {
        return true;
    }

    private Fahrenheit(AssistActivity act) {
        super(act, NAME,
              R.string.instruction_temperature,
              R.drawable.ic_temperature,
              R.string.summary_fahrenheit,
              R.string.manual_fahrenheit,
              EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected void run() {
        offer(() -> {});
    }

    @Override
    protected void run(String temperature) {
        FahrenheitConversion fahrenheit = Lang.fahrenheit(temperature, NAME);

        String oldDegrees = Maths.prettyNumber(fahrenheit.degrees);
        String unit = str(fahrenheit.fromKelvin ? R.string.kelvin : R.string.celsius);
        String fahrenheitDegrees = Maths.prettyNumber(fahrenheit.convert());
        giveText(str(R.string.fahrenheit_conversion, oldDegrees, unit, fahrenheitDegrees));
    }
}
