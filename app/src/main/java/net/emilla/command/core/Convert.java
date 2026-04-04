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
import net.emilla.measure.MeasureUnit;

import java.math.BigDecimal;
import java.math.BigInteger;

final class Convert extends CoreCommand {
    @internal Convert(Context ctx) {
        super(ctx, CoreEntry.CONVERT, EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected void run(AssistActivity act) {
        act.offer(a -> {});
    }

    @Override
    protected void run(AssistActivity act, String units) {
        ConversionRequest conversion = Lang.unitConversion(units);
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

        var res = act.getResources();
        String message = res.getString(
            R.string.unit_conversion,
            measurement(res, from, value),
            measurement(res, to, convert)
        );
        giveText(act, message);
    }

    private static String measurement(Resources res, MeasureUnit unit, BigDecimal value) {
        return res.getQuantityString(
            unit.plural(),
            // Todo lang: generally see if there's a way to get the "proper" pluralization of a
            //  fractional value
            value.remainder(BigDecimal.ONE).equals(BigDecimal.ZERO)
                ? clampToInt(value)
                // Todo lang: this doesn't actually comply with "last digit" rules for really big
                //  numbers.
                : Integer.MIN_VALUE
                // Todo lang: this probably doesn't always trigger the "other" plural
            ,
            Maths.prettyNumber(value)
        );
    }

    private static int clampToInt(BigDecimal value) {
        var bigInt = value.toBigInteger();
        int intValue = bigInt.intValue();
        return BigInteger.valueOf(intValue).equals(bigInt) ? intValue
             : bigInt.compareTo(BigInteger.ZERO) < 0 ? Integer.MIN_VALUE
             : Integer.MAX_VALUE
        ;
    }
}
