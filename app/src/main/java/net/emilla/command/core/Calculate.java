package net.emilla.command.core;

import static android.content.Intent.CATEGORY_APP_CALCULATOR;

import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.settings.Aliases;
import net.emilla.util.Calculator;

public class Calculate extends CategoryCommand {

    public static final String ENTRY = "calculate";
    @ArrayRes
    public static final int ALIASES = R.array.aliases_calculate;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    private static class CalculateParams extends CoreParams {

        private CalculateParams() {
            super(R.string.command_calculate,
                  R.string.instruction_calculate,
                  R.drawable.ic_calculate,
                  EditorInfo.IME_ACTION_DONE,
                  R.string.summary_calculate,
                  R.string.manual_calculate);
        }
    }

    public Calculate(AssistActivity act, String instruct) {
        super(act, instruct, new CalculateParams(), CATEGORY_APP_CALCULATOR);
    }

    @Override
    protected void run(String expression) {
        giveText(String.valueOf(Calculator.compute(expression)), true);
    }
}
