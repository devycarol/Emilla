package net.emilla.action.box;

import static net.emilla.chime.Chime.RESUME;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.emilla.Feedback;
import net.emilla.R;
import net.emilla.action.Gadget;
import net.emilla.activity.AssistActivity;
import net.emilla.sort.ItemSearchAdapter;
import net.emilla.util.Clipboard;
import net.emilla.util.Dialogs;
import net.emilla.wadget.ActionSurface;

public final class SnippetsFragment extends ActionBox implements Gadget {
    public SnippetsFragment() {
        super(R.layout.fragment_item_list);
    }

    public static SnippetsFragment newInstance() {
        return new SnippetsFragment();
    }

    private /*late*/ SharedPreferences mPrefs;

    private ItemSearchAdapter<Snippet> mAdapter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        var act = (AssistActivity) requireActivity();
        var recycler = (RecyclerView) view;

        var manager = new LinearLayoutManager(act);
        manager.setReverseLayout(true);

        recycler.setLayoutManager(manager);

        var inflater = act.getLayoutInflater();
        mPrefs = act.getSharedPreferences();
        mAdapter = Snippet.adapter(
            inflater,
            mPrefs,
            snippet -> act.take(Feedback.giveText(snippet.displayName, snippet.text(mPrefs)))
        );
        recycler.setAdapter(mAdapter);
    }

    public Feedback peek(String snippetLabel) {
        Snippet snippet = mAdapter.preferredItem(snippetLabel);
        return snippet != null
            ? Feedback.giveText(snippet.displayName, snippet.text(mPrefs))
            : Feedback.pend()
        ;
    }

    public Feedback copy(String snippetLabel) {
        Snippet snippet = mAdapter.preferredItem(snippetLabel);
        if (snippet == null) {
            return Feedback.pend();
        }

        Clipboard.copy(requireContext(), snippet.text(mPrefs));
        return Feedback.give();
    }

    public Feedback pop(String snippetLabel) {

        Snippet snippet = mAdapter.preferredItem(snippetLabel);
        if (snippet == null) {
            return Feedback.pend();
        }

        var ctx = requireContext();
        String text = snippet.text(mPrefs);
        snippet.delete(ctx, mPrefs);
        mAdapter.remove(snippet);
        Clipboard.copy(ctx, text);
        return Feedback.give();
    }

    public Feedback remove(String snippetLabel) {
        Snippet snippet = mAdapter.preferredItem(snippetLabel);
        if (snippet == null) {
            return Feedback.pend();
        }

        snippet.delete(requireContext(), mPrefs);
        mAdapter.remove(snippet);
        return Feedback.give();
    }

    public Feedback add(String snippetLabel, String text) {
        var snippet = new Snippet(snippetLabel);
        if (mAdapter.exactItem(snippetLabel) != null) {
            var act = (AssistActivity) requireActivity();
            var res = act.getResources();
            return Feedback.offer(
                Dialogs.dual(
                    act,
                    R.string.dialog_overwrite_snippet,
                    res.getString(
                        R.string.dlg_msg_overwrite_snippet,
                        snippetLabel
                    ),
                    R.string.overwrite,
                    (dlg, which) -> {
                        snippet.overwrite(act, mPrefs, text);
                        act.chime(RESUME);
                    }
                )
            );
        }

        snippet.saveNew(requireContext(), mPrefs, text);
        mAdapter.add(snippet);
        return Feedback.give();
    }

    @Override
    public void instruct(ActionSurface surface, @Nullable String instruction) {
        if (mAdapter != null) {
            // TODO: figure out how this damn life cycle works. The adapter should never be null
            //  while the fragment is alive, but this method may be called outside of that window.
            //  I want to actually understand when and how this happens and how to generally avoid
            //  it.
            mAdapter.search(instruction);
        }
    }
}
