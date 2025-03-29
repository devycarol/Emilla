package net.emilla.activity

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.net.Uri
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import net.emilla.app.AppList
import net.emilla.chime.Chimer.Companion.START
import net.emilla.config.SettingVals

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
    val appList = AppList(appCtx.packageManager)

    val attachmentMap: HashMap<String, ArrayList<Uri>?> by lazy { HashMap<String, ArrayList<Uri>?>() }
        @JvmName("attachmentMap") get

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

    private val chimer = SettingVals.chimer(appCtx, prefs)

    private var dontChimePend = false
    private var dontChimeResume = false
    private var dontChimeSuccess = false
    private var dontTryCancel = false

    init {
        chime(START)
    }

    fun chime(id: Byte) = chimer.chime(id)

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
