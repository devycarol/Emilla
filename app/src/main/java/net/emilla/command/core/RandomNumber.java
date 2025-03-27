package net.emilla.command.core;

import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.lang.Lang;
import net.emilla.settings.Aliases;
import net.emilla.util.Dialogs;

import java.util.Random;

public final class RandomNumber extends CoreCommand {

    public static final String ENTRY = "random_number";
    @StringRes
    public static final int NAME = R.string.command_random_number;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_random_number;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static Yielder yielder() {
        return new Yielder(true, RandomNumber::new, ENTRY, NAME, ALIASES);
    }

    private RandomNumber(AssistActivity act) {
        super(act, NAME,
              R.string.instruction_text,
              R.drawable.ic_random_number,
              R.string.summary_random_number,
              R.string.manual_random_number,
              EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected void run() {
        var rand = new Random();
        var msg = String.valueOf(rand.nextLong());
        giveDialog(Dialogs.message(activity, NAME, msg));
    }

    @Override
    protected void run(String range) {
        var randRange = Lang.randomRange(range, NAME);
        int inclusStart = randRange.inclusStart;
        int exclusEnd = randRange.exclusEnd;

        int negativeOffset = 0;
        boolean negative = inclusStart < 0;
        if (negative) {
            negativeOffset = inclusStart;
            exclusEnd -= inclusStart;
            inclusStart = 0;
        }

        var rand = new Random();
        int randVal = negativeOffset + inclusStart
                    + rand.nextInt(exclusEnd - inclusStart);
        var msg = String.valueOf(randVal);
        giveDialog(Dialogs.message(activity, NAME, msg));
    }
}
