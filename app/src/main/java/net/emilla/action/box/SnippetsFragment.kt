package net.emilla.action.box

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.emilla.R
import net.emilla.chime.Chimer.PEND
import net.emilla.chime.Chimer.RESUME
import net.emilla.command.core.Snippets
import net.emilla.run.Gift
import net.emilla.settings.SettingVals
import net.emilla.util.Dialogs

class SnippetsFragment : ActionBox(R.layout.snippet_item_list) {

    companion object {
        @JvmStatic
        fun newInstance(): SnippetsFragment = SnippetsFragment()
    }

    private val mViewModel: SnippetsViewModel by viewModels<SnippetsViewModel> {
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        SnippetsViewModel.Factory(prefs)
    }
    private val mAdapter: SnippetAdapter by lazy {
        SnippetAdapter(mViewModel.snippetLabels(), SnippetAdapter.OnItemClickListener { pos ->
            peek(mViewModel.labelAt(pos))
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (view !is RecyclerView) return

        val mgr = LinearLayoutManager(activity)
        mgr.setStackFromEnd(true)

        view.setLayoutManager(mgr)
        view.setAdapter(mAdapter)
    }

    fun contains(label: String) = mViewModel.snippetLabels().contains(label)

    val isEmpty get() = mViewModel.snippetLabels().isEmpty

    fun prime(action: Snippets.Action) {
        activity.chime(PEND)

        mAdapter.setItemClickAction(
        when (action) {
            Snippets.Action.PEEK -> { pos ->
                peek(mViewModel.labelAt(pos))
            }
            Snippets.Action.GET -> { pos ->
                val label = mViewModel.labelAt(pos)
                get(label)
                resetItemClickListener()
            }
            Snippets.Action.POP -> { pos ->
                val label = mViewModel.labelAt(pos)
                pop(label, label)
                resetItemClickListener()
            }
            Snippets.Action.REMOVE -> { pos ->
                val label = mViewModel.labelAt(pos)
                remove(label, label)
                give(Gift {})
                resetItemClickListener()
            }
            Snippets.Action.ADD -> { pos ->
                val label = mViewModel.labelAt(pos)
                val text = activity.dataText()
                if (text != null) tryAdd(label, text)
                else peek(label)
                resetItemClickListener()
            }
        })
    }

    private fun resetItemClickListener() {
        mAdapter.setItemClickAction(SnippetAdapter.OnItemClickListener { pos ->
            peek(mViewModel.labelAt(pos))
        })
    }

    fun peek(label: String) = giveMessage(SettingVals.snippet(mViewModel.prefs, label))

    fun get(label: String) = giveCopy(SettingVals.snippet(mViewModel.prefs, label))

    fun pop(label: String, lcLabel: String) {
        get(lcLabel)
        remove(label, lcLabel)
    }

    fun tryAdd(label: String, text: String) {
        if (contains(label)) {
            val msg = str(R.string.dlg_msg_overwrite_snippet, label)
            offerDialog(
                Dialogs.dual(activity, R.string.dialog_overwrite_snippet, msg, R.string.overwrite,
                DialogInterface.OnClickListener { dlg, which ->
                    mViewModel.replaceSnippet(label, text, mAdapter)
                    chime(RESUME)
                })
            )
        } else {
            mViewModel.addSnippet(label, text, mAdapter)
            give(Gift { toast(R.string.toast_saved) })
        }
    }

    fun remove(label: String, lcLabel: String) {
        mViewModel.removeSnippet(lcLabel, mAdapter)
        toast(R.string.toast_snippet_deleted, label)
    }

    @StringRes
    override fun name() = R.string.command_snippets
}
