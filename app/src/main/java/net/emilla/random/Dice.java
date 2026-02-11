package net.emilla.random;

import androidx.annotation.IntRange;

public record Dice(int rollCount, @IntRange(from = 1) int faceCount) {
    public static Dice modifier(int amount) {
        return new Dice(amount, 1);
    }
}
