package net.emilla.activity

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ResolveInfo
import android.content.res.Resources
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import net.emilla.chime.Chimer
import net.emilla.settings.SettingVals
import net.emilla.util.Apps

internal class AssistViewModel private constructor(appCtx: Context) : ViewModel() {

    /**
     * A factory for the assistant view model.
     *
     * @param appCtx it's important to use the application context to avoid memory leaks!
     */
    class Factory(private val appCtx: Context) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AssistViewModel(appCtx) as T
        }
    }

    @JvmField
    val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(appCtx)
    @JvmField
    val res: Resources = appCtx.resources

    @JvmField
    val noTitlebar = !SettingVals.showTitlebar(prefs, res)
    @JvmField
    val alwaysShowData = SettingVals.alwaysShowData(prefs)

    @JvmField
    val motd: String? = if (noTitlebar) null else SettingVals.motd(prefs, res)

    @JvmField
    val chimer: Chimer = SettingVals.chimer(appCtx, prefs)
    @JvmField
    val appList: List<ResolveInfo> = Apps.resolveList(appCtx.packageManager)

    @JvmField
    var noCommand = true
    @JvmField
    var dataAvailable = true
    @JvmField
    var dataVisible = alwaysShowData
    @JvmField
    var dialogOpen = false
    // todo: you can probably hard-code these UI-state properties into views, fragments, .. directly?

    @JvmField
    var imeAction = EditorInfo.IME_ACTION_NEXT
    // IME action is next with an empty command field

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
}
