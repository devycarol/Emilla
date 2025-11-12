package net.emilla.lang.phrase;

import java.util.Arrays;
import java.util.Random;

public final class Dices {

    private final Dice[] mDices;

    public Dices(Dice[] dices) {
        Arrays.sort(dices);
        mDices = dices;
    }

    public int roll(Random rand) {
        int result = 0;
        for (Dice dice : mDices) result += dice.roll(rand);
        return result;
    }
}
