package net.emilla.result;

import android.net.Uri;

import net.emilla.chime.Chime;

public record ChimeSoundResult(Chime chime, Uri soundUri) {}
