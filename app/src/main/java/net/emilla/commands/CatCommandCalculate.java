package net.emilla.commands;

import static android.content.Intent.CATEGORY_APP_CALCULATOR;

import android.view.inputmethod.EditorInfo;

import androidx.annotation.DrawableRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exceptions.EmlaAppsException;
import net.emilla.utils.Calculator;

public class CatCommandCalculate extends CatCommand {
@Override
public int imeAction() {
    return EditorInfo.IME_ACTION_DONE;
}

@Override @DrawableRes
public int icon() {
    return R.drawable.ic_calculate;
}

public CatCommandCalculate(AssistActivity act, String instruct) {
    super(act, instruct, CATEGORY_APP_CALCULATOR, R.string.command_calculate, R.string.instruction_calculate);
}

@Override
protected void noSuchApp() {
    throw new EmlaAppsException("No calculator app found for your device.");
}

@Override
protected void run(String expression) {
    give(String.valueOf(Calculator.compute(expression)), true);
}
}
