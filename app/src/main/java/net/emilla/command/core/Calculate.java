package net.emilla.command.core;

import static android.content.Intent.CATEGORY_APP_CALCULATOR;

import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.settings.Aliases;
import net.emilla.util.Apps;
import net.emilla.util.Calculator;

public class Calculate extends CategoryCommand {

    public static final String ENTRY = "calculate";
    @StringRes
    public static final int NAME = R.string.command_calculate;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_calculate;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static Yielder yielder() {
        return new Yielder(true, Calculate::new, ENTRY, NAME, ALIASES);
    }

    private static class CalculateParams extends CoreParams {

        private CalculateParams() {
            super(NAME,
                  R.string.instruction_calculate,
                  R.drawable.ic_calculate,
                  EditorInfo.IME_ACTION_DONE,
                  R.string.summary_calculate,
                  R.string.manual_calculate);
        }
    }

    public Calculate(AssistActivity act) {
        super(act, new CalculateParams());
    }

    @Override
    protected Intent makeFilter() {
        return Apps.categoryTask(CATEGORY_APP_CALCULATOR);
    }

    @Override
    protected void run(String expression) {
        giveText(String.valueOf(Calculator.compute(expression)), true);
    }
}
