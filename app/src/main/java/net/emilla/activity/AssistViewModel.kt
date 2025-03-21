package net.emilla.activity

import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.emilla.settings.SettingVals
import net.emilla.util.Apps

internal class AssistViewModel(
    pm: PackageManager,
    prefs: SharedPreferences,
    res: Resources
) : ViewModel() {

    @JvmField
    val noTitlebar = !SettingVals.showTitlebar(prefs, res)
    @JvmField
    val appList: List<ResolveInfo> = Apps.resolveList(pm)

    @JvmField
    var noCommand = true
    @JvmField
    var dialogOpen = false

    private var dontChimePend = false
    private var dontChimeResume = false
    private var dontChimeSuccess = false
    private var dontTryCancel = false

    fun suppressPendChime() {
        dontChimePend = true
    }

    fun suppressResumeChime() {
        dontChimeResume = true
    }

    fun suppressSuccessChime() {
        dontChimeSuccess = true
    }

    @Deprecated("use what the 'modern' navigation system wants instead of KEYCODE_BACK.")
    fun suppressBackCancellation() {
        dontTryCancel = true
    }

    fun askChimePend() = if (dontChimePend) {
        dontChimePend = false
        false
    } else true

    fun askChimeResume() = if (dontChimeResume) {
        dontChimeResume = false
        false
    } else true

    fun askChimeSuccess() = if (dontChimeSuccess) {
        dontChimeSuccess = false
        false
    } else true

    @Deprecated("use what the 'modern' navigation system wants instead of KEYCODE_BACK.")
    fun askTryCancel() = if (dontTryCancel) {
        dontTryCancel = false
        false
    } else true

    class Factory(
        private val pm: PackageManager,
        private val prefs: SharedPreferences,
        private val res: Resources
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AssistViewModel(pm, prefs, res) as T
        }
    }
}
