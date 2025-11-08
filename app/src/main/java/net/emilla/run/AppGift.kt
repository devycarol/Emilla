package net.emilla.run

import android.content.Intent
import net.emilla.R
import net.emilla.activity.AssistActivity
import net.emilla.activity.DummyActivity
import net.emilla.exception.EmillaException
import net.emilla.util.Apps

class AppGift(private val intent: Intent) : CommandRun {

    override fun run(act: AssistActivity) {
        if (intent.resolveActivity(act.packageManager) != null) {
            act.finishAndRemoveTask()
            val dummy: Intent = Apps.meTask(act, DummyActivity::class.java)
                .putExtra(Intent.EXTRA_INTENT, intent)
                .addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            act.startActivity(dummy)
        } else throw EmillaException(R.string.error, R.string.error_no_app)
    }
}
