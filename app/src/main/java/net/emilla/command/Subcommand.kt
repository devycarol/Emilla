package net.emilla.command;

import androidx.annotation.Nullable;

public record Subcommand<A extends Enum<A>>(A action, @Nullable String instruction) {}
