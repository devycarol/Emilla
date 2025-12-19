package net.emilla.command.core;

import android.content.Context;
import android.view.inputmethod.EditorInfo;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.lang.Lang;
import net.emilla.lang.measure.CelsiusConversion;
import net.emilla.math.Maths;

final class Celsius extends CoreCommand {

    @internal Celsius(Context ctx) {
        super(ctx, CoreEntry.CELSIUS, EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected void run(AssistActivity act) {
        act.offer(a -> {});
    }

    @Override
    protected void run(AssistActivity act, String temperature) {
        CelsiusConversion celsius = Lang.celsius(temperature, CoreEntry.CELSIUS.name);

        String oldDegrees = Maths.prettyNumber(celsius.degrees);
        var res = act.getResources();
        String unit = res.getString(celsius.fromKelvin ? R.string.kelvin : R.string.fahrenheit);
        String celsiusDegrees = Maths.prettyNumber(celsius.convert());
        giveText(act, res.getString(R.string.celsius_conversion, oldDegrees, unit, celsiusDegrees));
    }

}
