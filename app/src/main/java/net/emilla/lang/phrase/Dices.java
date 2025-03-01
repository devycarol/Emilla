package net.emilla.lang.phrase;

import java.util.Random;

public final class Dices {

    private final Iterable<Dice> mDices;

    public Dices(Iterable<Dice> dices) {
        mDices = dices;
    }

    public int roll(Random rand) {
        int result = 0;
        for (Dice dice : mDices) result += dice.roll(rand);
        return result;
    }
}
