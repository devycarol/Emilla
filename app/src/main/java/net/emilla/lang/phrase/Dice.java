package net.emilla.lang.phrase;

import androidx.annotation.Nullable;

import net.emilla.util.Hashes;

import java.util.random.RandomGenerator;

public final class Dice implements Comparable<Dice> {

    private int mCount;
    private final int mFaces;

    public Dice(int count, int faces) {
        mCount = count;
        mFaces = faces;
    }

    public void add(int count) {
        mCount += count;
    }

    public int count() {
        return mCount;
    }

    public int roll(RandomGenerator rand) {
        if (mFaces == 1) {
            return mCount;
        }

        int result = 0;
        if (mCount >= 0) {
            for (int i = 0; i < mCount; ++i) {
                result += rollOne(rand);
            }
        } else {
            for (int i = 0; i < -mCount; ++i) {
                result -= rollOne(rand);
            }
        }

        return result;
    }

    private int rollOne(RandomGenerator rand) {
        return rand.nextInt(mFaces) + 1;
    }

    @Override
    public int compareTo(Dice other) {
        return Integer.compare(mFaces, other.mFaces);
    }

    @Override
    public boolean equals(@Nullable Object other) {
        return other instanceof Dice dice
            && mFaces == dice.mFaces;
    }

    @Override
    public int hashCode() {
        return Hashes.one(mFaces);
    }

}
