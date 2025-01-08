package net.emilla.command.core;

import static android.content.Intent.CATEGORY_APP_CALCULATOR;

import android.view.inputmethod.EditorInfo;

import androidx.annotation.DrawableRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.utils.Calculator;

public class CateCalculate extends CategoryCommand {

    public static final String ENTRY = "calculate";

    @Override
    public int imeAction() {
        return EditorInfo.IME_ACTION_DONE;
    }

    @Override @DrawableRes
    public int icon() {
        return R.drawable.ic_calculate;
    }

    public CateCalculate(AssistActivity act, String instruct) {
        super(act, instruct, CATEGORY_APP_CALCULATOR, R.string.command_calculate, R.string.instruction_calculate);
    }

    @Override
    protected void run(String expression) {
        giveText(String.valueOf(Calculator.compute(expression)), true);
    }
}
