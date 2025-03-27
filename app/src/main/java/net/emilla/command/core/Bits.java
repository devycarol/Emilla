package net.emilla.command.core;

import static android.content.Intent.CATEGORY_APP_CALCULATOR;

import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.app.Apps;
import net.emilla.math.BitwiseCalculator;
import net.emilla.settings.Aliases;

public final class Bits extends CategoryCommand {

    public static final String ENTRY = "bits";
    @StringRes
    public static final int NAME = R.string.command_bits;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_bits;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static Yielder yielder() {
        return new Yielder(true, Bits::new, ENTRY, NAME, ALIASES);
    }

    private Bits(AssistActivity act) {
        super(act, NAME,
              R.string.instruction_calculate,
              R.drawable.ic_command,
              R.string.summary_bits,
              R.string.manual_bits,
              EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected Intent makeFilter() {
        return Apps.categoryTask(CATEGORY_APP_CALCULATOR);
    }

    @Override
    protected void run(String expression) {
        giveMessage(String.valueOf(BitwiseCalculator.compute(expression, NAME)));
    }
}
