package net.emilla.command.core;

import android.content.Context;
import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.math.BitwiseCalculator;

final class Bits extends CoreCommand {
    @internal Bits(Context ctx) {
        super(ctx, CoreEntry.BITS, EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected void run(AssistActivity act) {
        CategoryCommand.run(act, Intent.CATEGORY_APP_CALCULATOR);
    }

    @Override
    protected void run(AssistActivity act, String expression) {
        giveText(act, String.valueOf(BitwiseCalculator.compute(expression, CoreEntry.BITS.name)));
    }
}
