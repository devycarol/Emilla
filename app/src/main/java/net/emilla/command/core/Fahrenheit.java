package net.emilla.command.core;

import android.content.Context;
import android.content.res.Resources;
import android.view.inputmethod.EditorInfo;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.lang.Lang;
import net.emilla.math.Maths;
import net.emilla.measure.ConversionRequest;
import net.emilla.measure.FahrenheitConversion;
import net.emilla.measure.MeasureUnit;

import java.math.BigDecimal;

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
            fail(act, R.string.error_invalid_conversion);
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

        if (false) {
            ConversionRequest conversion = Lang.unitConversion(temperature);
            if (conversion == null) {
                fail(act, R.string.error_invalid_conversion);
                return;
            }

            MeasureUnit from = conversion.from();
            BigDecimal value = conversion.value();
            MeasureUnit to = conversion.to();
            BigDecimal convert = from.convert(value, to);
            if (convert == null) {
                fail(act, R.string.error_invalid_conversion);
                return;
            }

            String message = res.getString(
                R.string.unit_conversion,
                measurement(res, from, value),
                measurement(res, to, convert)
            );
            giveText(act, message);
        }
    }

    private static String measurement(Resources res, MeasureUnit unit, BigDecimal value) {
        return res.getQuantityString(unit.plural(), value.intValue(), value);
    }
}
