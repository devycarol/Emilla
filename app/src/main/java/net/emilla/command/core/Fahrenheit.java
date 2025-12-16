package net.emilla.command.core;

import android.content.Context;
import android.view.inputmethod.EditorInfo;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.lang.Lang;
import net.emilla.lang.measure.FahrenheitConversion;
import net.emilla.math.Maths;

final class Fahrenheit extends CoreCommand {

    /*internal*/ Fahrenheit(Context ctx) {
        super(ctx, CoreEntry.FAHRENHEIT, EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected void run(AssistActivity act) {
        act.offer(a -> {});
    }

    @Override
    protected void run(AssistActivity act, String temperature) {
        var res = act.getResources();

        FahrenheitConversion fahrenheit = Lang.fahrenheit(temperature, CoreEntry.FAHRENHEIT.name);

        String oldDegrees = Maths.prettyNumber(fahrenheit.degrees);
        String unit = res.getString(fahrenheit.fromKelvin ? R.string.kelvin : R.string.celsius);
        String fahrenheitDegrees = Maths.prettyNumber(fahrenheit.convert());
        giveText(act, res.getString(R.string.fahrenheit_conversion, oldDegrees, unit, fahrenheitDegrees));
    }

}
