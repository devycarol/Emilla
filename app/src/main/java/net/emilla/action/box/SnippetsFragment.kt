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
import net.emilla.chime.Chimer.Companion.PEND
import net.emilla.chime.Chimer.Companion.RESUME
import net.emilla.command.core.Snippets
import net.emilla.config.SettingVals
import net.emilla.run.Gift
import net.emilla.util.Dialogs

class SnippetsFragment : ActionBox(R.layout.snippet_item_list) {

    companion object {
        @JvmStatic
        fun newInstance() = SnippetsFragment()
    }

    private val vm: SnippetsViewModel by viewModels<SnippetsViewModel> {
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        SnippetsViewModel.Factory(prefs)
    }

    private val adapter: SnippetAdapter by lazy {
        SnippetAdapter(vm.snippetLabels(), SnippetAdapter.OnItemClickListener { pos ->
            peek(vm.labelAt(pos))
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view as RecyclerView

        val mgr = LinearLayoutManager(activity)
        mgr.setStackFromEnd(true)

        view.setLayoutManager(mgr)
        view.setAdapter(adapter)
    }

    fun contains(label: String) = vm.snippetLabels().contains(label)

    val isEmpty get() = vm.snippetLabels().isEmpty

    fun prime(action: Snippets.Action) {
        activity.chime(PEND)

        adapter.setItemClickAction(
        when (action) {
            Snippets.Action.PEEK -> { pos ->
                peek(vm.labelAt(pos))
            }
            Snippets.Action.GET -> { pos ->
                val label = vm.labelAt(pos)
                get(label)
                resetItemClickListener()
            }
            Snippets.Action.POP -> { pos ->
                val label = vm.labelAt(pos)
                pop(label, label)
                resetItemClickListener()
            }
            Snippets.Action.REMOVE -> { pos ->
                val label = vm.labelAt(pos)
                remove(label, label)
                give(Gift {})
                resetItemClickListener()
            }
            Snippets.Action.ADD -> { pos ->
                val label = vm.labelAt(pos)
                val text = activity.dataText()
                if (text != null) tryAdd(label, text)
                else peek(label)
                resetItemClickListener()
            }
        })
    }

    private fun resetItemClickListener() {
        adapter.setItemClickAction(SnippetAdapter.OnItemClickListener { pos ->
            peek(vm.labelAt(pos))
        })
    }

    fun peek(label: String) = giveMessage(SettingVals.snippet(vm.prefs, label))

    fun get(label: String) = giveCopy(SettingVals.snippet(vm.prefs, label))

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
                    vm.replaceSnippet(label, text, adapter)
                    chime(RESUME)
                })
            )
        } else {
            vm.addSnippet(label, text, adapter)
            give(Gift { toast(R.string.toast_saved) })
        }
    }

    fun remove(label: String, lcLabel: String) {
        vm.removeSnippet(lcLabel, adapter)
        toast(R.string.toast_snippet_deleted, label)
    }

    @StringRes
    override val name = R.string.command_snippets
}
