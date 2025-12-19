package net.emilla.chime;

import android.content.Context;

import net.emilla.annotation.internal;

final class Silence implements Chimer {

    @internal Silence() {}

    @Override
    public void chime(Context ctx, Chime chime) {}

}
