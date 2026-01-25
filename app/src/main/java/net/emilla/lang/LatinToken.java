package net.emilla.lang;

import net.emilla.annotation.internal;

public sealed abstract class LatinToken permits Letter, Word {
    @internal final boolean mRequireSpaceBefore;
    protected final boolean ignoreCase;

    @internal LatinToken(boolean requireSpaceBefore, boolean ignoreCase) {
        mRequireSpaceBefore = requireSpaceBefore;
        this.ignoreCase = ignoreCase;
    }
}
