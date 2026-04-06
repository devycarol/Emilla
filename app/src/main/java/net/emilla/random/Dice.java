package net.emilla.random;

import androidx.annotation.IntRange;

record Dice(int rollCount, @IntRange(from = 1) int faceCount) {
    public static Dice modifier(int amount) {
        return new Dice(amount, 1);
    }

    public Dice negate() {
        return new Dice(-rollCount, faceCount);
    }
}
