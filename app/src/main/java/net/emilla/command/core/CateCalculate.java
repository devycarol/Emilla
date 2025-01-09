package net.emilla.command.core;

import static android.content.Intent.CATEGORY_APP_CALCULATOR;

import android.view.inputmethod.EditorInfo;

import androidx.annotation.DrawableRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.utils.Calculator;

public class CateCalculate extends CategoryCommand {

    public static final String ENTRY = "calculate";

    private static class CalculateParams extends CoreParams {

        private CalculateParams() {
            super(R.string.command_calculate,
                  R.string.instruction_calculate,
                  R.drawable.ic_calculate,
                  EditorInfo.IME_ACTION_DONE);
        }
    }

    public CateCalculate(AssistActivity act, String instruct) {
        super(act, instruct, new CalculateParams(), CATEGORY_APP_CALCULATOR);
    }

    @Override
    protected void run(String expression) {
        giveText(String.valueOf(Calculator.compute(expression)), true);
    }
}
