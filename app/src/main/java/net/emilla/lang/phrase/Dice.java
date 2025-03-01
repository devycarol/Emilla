package net.emilla.lang.phrase;

import java.util.Objects;
import java.util.Random;

public final class Dice implements Comparable<Dice> {

    private int mCount;
    public final int faces;

    public Dice(int count, int faces) {
        mCount = count;
        this.faces = faces;
    }

    public void add(int count) {
        mCount += count;
    }

    public int count() {
        return mCount;
    }

    public int roll(Random rand) {
        if (faces == 1) return mCount;

        int result = 0;
        if (mCount >= 0) {
            for (int i = 0; i < mCount; ++i) result += rand.nextInt(faces) + 1;
        } else {
            for (int i = 0; i > mCount; --i) result -= rand.nextInt(faces) + 1;
        }

        return result;
    }

    @Override
    public int compareTo(Dice that) {
        return faces - that.faces;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (!(that instanceof Dice dice)) return false;
        return faces == dice.faces;
    }

    @Override
    public int hashCode() {
        return Objects.hash(faces);
    }
}
