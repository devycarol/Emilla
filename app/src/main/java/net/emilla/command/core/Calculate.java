package net.emilla.command.core;

import static android.content.Intent.CATEGORY_APP_CALCULATOR;

import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.apps.Apps;
import net.emilla.math.Calculator;
import net.emilla.math.Maths;

public final class Calculate extends CategoryCommand {

    public static final String ENTRY = "calculate";
    @StringRes
    public static final int NAME = R.string.command_calculate;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_calculate;

    public static Yielder yielder() {
        return new Yielder(CoreEntry.CALCULATE, true);
    }

    public static boolean possible() {
        return true;
    }

    /*internal*/ Calculate(AssistActivity act) {
        super(act, CoreEntry.CALCULATE, EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected Intent makeFilter() {
        return Apps.categoryTask(CATEGORY_APP_CALCULATOR);
    }

    @Override
    protected void run(String expression) {
        giveText(Maths.prettyNumber(Calculator.compute(expression, NAME)));
    }
}
