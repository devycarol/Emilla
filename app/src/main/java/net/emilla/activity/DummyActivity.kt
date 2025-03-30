package net.emilla.activity

import android.content.Context
import android.content.Intent
import android.content.Intent.EXTRA_INTENT
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.preference.PreferenceManager
import net.emilla.chime.Chimer
import net.emilla.chime.Chimer.Companion.SUCCEED
import net.emilla.config.SettingVals

class DummyActivity : EmillaActivity() {

    private lateinit var chimer: Chimer

    private val callback = ActivityResultCallback<ActivityResult> {
        finishAndRemoveTask()
        chimer.chime(SUCCEED)
    }

    private val resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        StartActivityForResult(), callback
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent.getParcelableExtra<Intent>(EXTRA_INTENT) ?: return

        val appCtx: Context = applicationContext
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(appCtx)
        chimer = SettingVals.chimer(appCtx, prefs)

        resultLauncher.launch(intent)
    }
}

