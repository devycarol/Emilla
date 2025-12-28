package net.emilla.lang;

import net.emilla.annotation.internal;
import net.emilla.annotation.open;

public sealed @open class LatinToken permits Letter, Word {

    @internal final boolean mRequireSpaceBefore;
    protected final boolean ignoreCase;

    @internal LatinToken(boolean requireSpaceBefore, boolean ignoreCase) {
        mRequireSpaceBefore = requireSpaceBefore;
        this.ignoreCase = ignoreCase;
    }

}
