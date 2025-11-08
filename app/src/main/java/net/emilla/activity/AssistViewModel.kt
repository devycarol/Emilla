package net.emilla.activity

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.net.Uri
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import net.emilla.chime.Chime
import net.emilla.chime.Chime.START
import net.emilla.chime.Chimer
import net.emilla.config.SettingVals
import net.emilla.util.AppList

internal class AssistViewModel private constructor(private val appContext: Context) : ViewModel() {

    /**
     * A factory for the assistant view model.
     *
     * @param appContext it's important to use the application context to avoid memory leaks!
     */
    class Factory(private val appContext: Context) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AssistViewModel(appContext) as T
        }

    }

    @JvmField
    val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext)
    @JvmField
    val res: Resources = appContext.resources

    @JvmField
    val noTitlebar = !SettingVals.showTitlebar(prefs, res)
    @JvmField
    val alwaysShowData = SettingVals.alwaysShowData(prefs)

    @JvmField
    val motd: String? = if (noTitlebar) null else SettingVals.motd(prefs, res)
    @JvmField
    val appList = AppList.launchers(appContext.packageManager)

    val attachmentMap = HashMap<String, ArrayList<Uri>?>()
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

    private val chimer = Chimer.of(prefs)

    private var dontChimePend = false
    private var dontChimeResume = false
    private var dontChimeSuccess = false
    private var dontTryCancel = false

    init {
        chime(START)
    }

    fun chime(chime: Chime) = chimer.chime(appContext, chime)

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
