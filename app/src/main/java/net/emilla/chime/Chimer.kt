package net.emilla.chime

import android.content.SharedPreferences
import android.net.Uri
import androidx.core.net.toUri

fun interface Chimer {
    fun chime(id: Byte)

    companion object {
        // IDs
        const val START: Byte = 0
        const val ACT: Byte = 1
        const val PEND: Byte = 2
        const val RESUME: Byte = 3
        const val EXIT: Byte = 4
        const val SUCCEED: Byte = 5
        const val FAIL: Byte = 6

        // Sound sets
        const val NONE: String = "none"
        const val NEBULA: String = "nebula"
        const val VOICE_DIALER: String = "voice_dialer"
        const val CUSTOM: String = "custom"

        // Preference keys
        const val SOUND_SET: String = "sound_set"
        const val PREF_START: String = "chime_start"
        const val PREF_ACT: String = "chime_act"
        const val PREF_PEND: String = "chime_pend"
        const val PREF_RESUME: String = "chime_resume"
        const val PREF_EXIT: String = "chime_exit"
        const val PREF_SUCCEED: String = "chime_succeed"
        const val PREF_FAIL: String = "chime_fail"

        internal fun customSounds(prefs: SharedPreferences) = arrayOf<Uri?>(
            soundUri(prefs, PREF_START),
            soundUri(prefs, PREF_ACT),
            soundUri(prefs, PREF_PEND),
            soundUri(prefs, PREF_RESUME),
            soundUri(prefs, PREF_EXIT),
            soundUri(prefs, PREF_SUCCEED),
            soundUri(prefs, PREF_FAIL)
        )

        private fun soundUri(prefs: SharedPreferences, prefKey: String?): Uri? {
            return prefs.getString(prefKey, null)?.toUri()
        }
    }
}
