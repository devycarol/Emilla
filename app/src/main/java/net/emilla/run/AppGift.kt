package net.emilla.run

import android.app.Activity
import android.content.Intent
import net.emilla.R
import net.emilla.activity.DummyActivity
import net.emilla.app.Apps
import net.emilla.exception.EmillaException

class AppGift(private val activity: Activity, private val intent: Intent) : Gift {

    override fun run() {
        if (intent.resolveActivity(activity.packageManager) != null) {
            activity.finishAndRemoveTask()
            val dummy: Intent = Apps.meTask(activity, DummyActivity::class.java)
                .putExtra(Intent.EXTRA_INTENT, intent)
                .addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            activity.startActivity(dummy)
        } else throw EmillaException(R.string.error, R.string.error_no_app)
    }
}
