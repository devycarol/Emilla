package net.emilla.command.app;

import androidx.annotation.ArrayRes;

import net.emilla.AssistActivity;
import net.emilla.R;

public final class Tubular extends VideoSearchBySend {

    public static final String PKG = "org.polymorphicshade.tubular";
    @ArrayRes
    public static final int ALIASES = R.array.aliases_tubular;

    public Tubular(AssistActivity act, Yielder info) {
        super(act, info);
    }
}
