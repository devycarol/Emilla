package net.emilla.command;

import androidx.annotation.Nullable;

public final class Subcommand<A extends Enum<A>> {

    public final A action;
    @Nullable
    public final String instruction;

    public Subcommand(A action, @Nullable String instruction) {
        this.action = action;
        this.instruction = instruction;
    }

}
