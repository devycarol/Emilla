package net.emilla.command.core;

import static net.emilla.chime.Chime.PEND;

import android.content.Context;

import androidx.annotation.Nullable;

import net.emilla.R;
import net.emilla.action.box.SnippetsFragment;
import net.emilla.activity.AssistActivity;
import net.emilla.command.ActionMap;
import net.emilla.command.Subcommand;

/*internal*/ final class Snippets extends CoreDataCommand {

    private final SnippetsFragment mSnippetsFragment;

    private final ActionMap<SnippetAction> mActionMap;
    private SnippetAction mAction = SnippetAction.GET;

    /*internal*/ Snippets(Context ctx) {
        super(ctx, CoreEntry.SNIPPETS, R.string.data_hint_text);

        mSnippetsFragment = SnippetsFragment.newInstance();

        giveGadgets(mSnippetsFragment);

        var res = ctx.getResources();
        mActionMap = new ActionMap<SnippetAction>(res, SnippetAction.GET, SnippetAction[]::new);

        mActionMap.put(res, SnippetAction.PEEK, R.array.subcmd_snippet_peek, true);
        mActionMap.put(res, SnippetAction.GET, R.array.subcmd_snippet_get, true);
        mActionMap.put(res, SnippetAction.POP, R.array.subcmd_snippet_pop, true);
        mActionMap.put(res, SnippetAction.REMOVE, R.array.subcmd_snippet_remove, true);
    }

    @Override
    protected void onInstruct(@Nullable String person) {
        super.onInstruct(extractAction(person));
    }

    @Nullable
    private String extractAction(@Nullable String person) {
        if (person == null) {
            mAction = SnippetAction.GET;
            return null;
        }

        Subcommand<SnippetAction> subcmd = mActionMap.get(person);
        mAction = subcmd.action;

        return subcmd.instruction;
    }

    @Override
    protected void run(AssistActivity act) {
        act.chime(PEND);
    }

    @Override
    protected void run(AssistActivity act, String label) {
        label = extractAction(label);
        if (label == null) {
            run(act);
            return;
        }

        switch (mAction) {
        case PEEK -> mSnippetsFragment.peek(label);
        case GET -> mSnippetsFragment.copy(label);
        case POP -> mSnippetsFragment.pop(label);
        case REMOVE -> mSnippetsFragment.remove(label);
        }
    }

    @Override
    public void runWithData(AssistActivity act, String text) {
        run(act);
    }

    @Override
    public void runWithData(AssistActivity act, String label, String text) {
        mSnippetsFragment.add(label, text);
    }

}
