package net.emilla.command.core;

import android.content.Context;
import android.view.inputmethod.EditorInfo;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.lang.Lang;
import net.emilla.math.Maths;
import net.emilla.measure.FahrenheitConversion;

final class Fahrenheit extends CoreCommand {
    @internal Fahrenheit(Context ctx) {
        super(ctx, CoreEntry.FAHRENHEIT, EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected void run(AssistActivity act) {
        act.offer(a -> {});
    }

    @Override
    protected void run(AssistActivity act, String temperature) {
        FahrenheitConversion fahrenheit = Lang.toFahrenheit(temperature);
        if (fahrenheit == null) {
            fail(act, R.string.error_bad_temperature);
            return;
        }

        String oldDegrees = Maths.prettyNumber(fahrenheit.degrees());
        var res = act.getResources();
        String unit = res.getString(
            fahrenheit.wasFromKelvin()
                ? R.string.kelvin
                : R.string.celsius

        );
        String fahrenheitDegrees = Maths.prettyNumber(fahrenheit.convert());
        giveText(act, res.getString(R.string.fahrenheit_conversion, oldDegrees, unit, fahrenheitDegrees));
    }
}
