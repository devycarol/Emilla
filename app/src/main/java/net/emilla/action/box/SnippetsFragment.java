package net.emilla.action.box;

import static net.emilla.chime.Chime.PEND;
import static net.emilla.chime.Chime.RESUME;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.databinding.FragmentItemListBinding;
import net.emilla.sort.ItemSearchAdapter;
import net.emilla.util.Dialogs;

public final class SnippetsFragment extends ActionBox {

    @internal SnippetsFragment() {
        super(R.layout.fragment_item_list);
    }

    public static SnippetsFragment newInstance() {
        return new SnippetsFragment();
    }

    private /*late*/ SharedPreferences mPrefs;

    private ItemSearchAdapter<Snippet> mAdapter;

    @Override
    public View onCreateView(
        LayoutInflater inflater,
        @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState
    ) {
        var binding = FragmentItemListBinding.inflate(inflater, container, false);
        return binding.recycler;
    }

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
            snippet -> giveText(act, snippet.displayName, snippet.text(mPrefs))
        );
        recycler.setAdapter(mAdapter);
    }

    @Nullable
    private Snippet selectedSnippet(AssistActivity act, String search) {
        Snippet selected = mAdapter.preferredItem(search);
        if (selected == null) {
            act.chime(PEND);
        }
        return selected;
    }

    public void peek(String snippetLabel) {
        var act = (AssistActivity) requireActivity();

        Snippet snippet = selectedSnippet(act, snippetLabel);
        if (snippet != null) {
            giveText(act, snippet.displayName, snippet.text(mPrefs));
        }
    }

    public void copy(String snippetLabel) {
        var act = (AssistActivity) requireActivity();

        Snippet snippet = selectedSnippet(act, snippetLabel);
        if (snippet != null) {
            giveCopy(act, snippet.text(mPrefs));
        }
    }

    public void pop(String snippetLabel) {
        var act = (AssistActivity) requireActivity();

        Snippet snippet = selectedSnippet(act, snippetLabel);
        if (snippet != null) {
            snippet.delete(act, mPrefs);
            mAdapter.remove(snippet);
            giveCopy(act, snippet.text(mPrefs));
        }
    }

    public void remove(String snippetLabel) {
        var act = (AssistActivity) requireActivity();

        Snippet snippet = selectedSnippet(act, snippetLabel);
        if (snippet != null) {
            snippet.delete(act, mPrefs);
            mAdapter.remove(snippet);
            act.give(a -> {});
        }
    }

    public void add(String snippetLabel, String text) {
        var act = (AssistActivity) requireActivity();

        var snippet = new Snippet(snippetLabel);
        if (mAdapter.exactItem(snippetLabel) != null) {
            var res = act.getResources();
            offerDialog(
                act, Dialogs.dual(
                    act, R.string.dialog_overwrite_snippet,

                    res.getString(R.string.dlg_msg_overwrite_snippet, snippetLabel),
                    R.string.overwrite,
                    (dlg, which) -> {
                        snippet.overwrite(act, mPrefs, text);
                        act.chime(RESUME);
                    }
                )
            );
        } else {
            snippet.saveNew(act, mPrefs, text);
            mAdapter.add(snippet);
            act.give(a -> {});
        }
    }

    @Override
    public void instruct(@Nullable String instruction) {
        if (mAdapter != null) {
            // TODO: figure out how this damn life cycle works. The adapter should never be null
            //  while the fragment is alive, but this method may be called outside of that window.
            //  I want to actually understand when and how this happens and how to generally avoid
            //  it.
            mAdapter.search(instruction);
        }
    }

}
