package net.emilla.random;

import android.util.SparseIntArray;

import net.emilla.util.Booleans;

import java.util.Random;

public final class DiceRoller {
    private final SparseIntArray mDice = new SparseIntArray();

    public DiceRoller() {
    }

    public void add(Dice dice) {
        add(dice.rollCount(), dice.faceCount());
    }

    private void add(int rollCount, int faceCount) {
        rollCount += mDice.get(faceCount);
        if (rollCount != 0) {
            mDice.put(faceCount, rollCount);
        } else {
            mDice.delete(faceCount);
        }
    }

    public int roll(Random rand) {
        int size = mDice.size();
        if (size == 0) {
            return 0;
        }

        int roll = 0;
        boolean hasModifier = mDice.keyAt(0) == 1;
        if (hasModifier) {
            roll += mDice.valueAt(0);
        }

        for (int i = Booleans.bit(hasModifier); i < size; ++i) {
            int rollCount = mDice.valueAt(i);
            int faceCount = mDice.keyAt(i);
            if (rollCount > 0) {
                for (int k = 0; k < rollCount; ++k) {
                    roll += roll(rand, faceCount);
                }
            } else {
                for (int k = 0; k > rollCount; --k) {
                    roll -= roll(rand, faceCount);
                }
            }
        }

        return roll;
    }

    private static int roll(Random rand, int faceCount) {
        return rand.nextInt(faceCount) + 1;
    }
}
