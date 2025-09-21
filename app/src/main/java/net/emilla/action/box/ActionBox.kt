package net.emilla.action.box

import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import net.emilla.activity.AssistActivity
import net.emilla.run.CopyGift
import net.emilla.run.DialogRun
import net.emilla.run.Gift
import net.emilla.run.Offering
import net.emilla.run.TextGift

abstract class ActionBox protected constructor(
    @LayoutRes contentLayoutId: Int
) : Fragment (contentLayoutId) {

    protected val activity by lazy { requireActivity() as AssistActivity }
    private val res by lazy { activity.resources }

    protected fun chime(id: Byte) = activity.chime(id)
    protected fun toast(msg: CharSequence) = activity.toast(msg)
    protected fun toast(@StringRes msg: Int, vararg formatArgs: Any) = activity.toast(str(msg, *formatArgs))
    protected fun str(@StringRes id: Int, vararg formatArgs: Any) = res.getString(id, *formatArgs)
    private fun offer(offering: Offering) = activity.offer(offering)
    protected fun offerDialog(dlg: AlertDialog.Builder) = offer(DialogRun(activity, dlg))
    protected fun give(gift: Gift) = activity.give(gift)
    protected fun giveText(msg: CharSequence) = give(TextGift(activity, name, msg))
    protected fun giveCopy(text: String) = give(CopyGift(activity, text))

    @get:StringRes
    protected abstract val name: Int
}
