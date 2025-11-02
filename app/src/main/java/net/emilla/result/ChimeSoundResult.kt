package net.emilla.result

import android.net.Uri
import net.emilla.chime.Chime

data class ChimeSoundResult(@JvmField val chime: Chime, @JvmField val soundUri: Uri?)
