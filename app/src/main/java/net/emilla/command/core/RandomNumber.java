package net.emilla.command.core;

import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.lang.Lang;
import net.emilla.lang.phrase.RandRange;

import java.util.Random;

public final class RandomNumber extends CoreCommand {

    public static final String ENTRY = "random_number";
    @StringRes
    public static final int NAME = R.string.command_random_number;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_random_number;

    public static Yielder yielder() {
        return new Yielder(true, RandomNumber::new, ENTRY, NAME, ALIASES);
    }

    public static boolean possible() {
        return true;
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
        giveText(String.valueOf(rand.nextLong()));
    }

    @Override
    protected void run(String range) {
        RandRange randRange = Lang.randomRange(range, NAME);
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
        giveText(String.valueOf(randVal));
    }
}
