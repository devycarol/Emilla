package net.emilla.command.core;

import android.view.inputmethod.EditorInfo;

import net.emilla.activity.AssistActivity;
import net.emilla.lang.Lang;
import net.emilla.lang.phrase.RandRange;

import java.util.Random;

/*internal*/ final class RandomNumber extends CoreCommand {

    public static final String ENTRY = "random_number";

    public static boolean possible() {
        return true;
    }

    /*internal*/ RandomNumber(AssistActivity act) {
        super(act, CoreEntry.RANDOM_NUMBER, EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected void run() {
        var rand = new Random();
        giveText(String.valueOf(rand.nextLong()));
    }

    @Override
    protected void run(String range) {
        RandRange randRange = Lang.randomRange(range, CoreEntry.RANDOM_NUMBER.name);
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
