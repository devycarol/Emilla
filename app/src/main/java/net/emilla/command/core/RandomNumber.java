package net.emilla.command.core;

import android.content.Context;
import android.view.inputmethod.EditorInfo;

import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.lang.Lang;
import net.emilla.lang.phrase.RandRange;

import java.util.Random;

final class RandomNumber extends CoreCommand {

    @internal RandomNumber(Context ctx) {
        super(ctx, CoreEntry.RANDOM_NUMBER, EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected void run(AssistActivity act) {
        var rand = new Random();
        giveText(act, String.valueOf(rand.nextLong()));
    }

    @Override
    protected void run(AssistActivity act, String range) {
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
        giveText(act, String.valueOf(randVal));
    }

}
