package net.emilla.command.core;

import androidx.annotation.Nullable;

import net.emilla.R;
import net.emilla.action.box.SnippetsFragment;
import net.emilla.activity.AssistActivity;
import net.emilla.command.ActionMap;
import net.emilla.command.Subcommand;

/*internal*/ final class Snippets extends CoreDataCommand {

    public static final String ENTRY = "snippets";

    public static boolean possible() {
        return true;
    }

    private SnippetsFragment mSnippetsFragment = null;

    private ActionMap<SnippetAction> mActionMap = null;
    private SnippetAction mAction = SnippetAction.GET;
    @Nullable
    private String mUsedSnippet = null;
    @Nullable
    private String mUsedText = null;

    /*internal*/ Snippets(AssistActivity act) {
        super(act, CoreEntry.SNIPPETS, R.string.data_hint_text);
    }

    @Override
    protected void onInit() {
        super.onInit();

        if (mSnippetsFragment == null) mSnippetsFragment = SnippetsFragment.newInstance();
        this.activity.giveActionBox(mSnippetsFragment);

        if (mActionMap == null) {
            mActionMap = new ActionMap<SnippetAction>(SnippetAction.GET);

            mActionMap.put(this.resources, SnippetAction.PEEK, R.array.subcmd_snippet_peek, true);
            mActionMap.put(this.resources, SnippetAction.GET, R.array.subcmd_snippet_get, true);
            mActionMap.put(this.resources, SnippetAction.POP, R.array.subcmd_snippet_pop, true);
            mActionMap.put(this.resources, SnippetAction.REMOVE, R.array.subcmd_snippet_remove, true);
        }
    }

    @Override
    protected void onClean() {
        super.onClean();

        this.activity.removeActionBox(mSnippetsFragment);
        mSnippetsFragment = null;

        mUsedSnippet = null;
        mUsedText = null;
        // forget the used snippet
    }

    @Override
    protected void run() {
        refreshState(SnippetAction.GET);
        snippet(new Subcommand<SnippetAction>(SnippetAction.GET, null));
    }

    @Override
    protected void run(String label) {
        Subcommand<SnippetAction> subcmd = mActionMap.get(label);
        refreshState(subcmd.action);
        snippet(subcmd);
    }

    private void refreshState(SnippetAction action) {
        if (mAction != action) {
            mAction = action;
            mUsedSnippet = null;
            mUsedText = null;
            // forget the used snippet
        } else if (action == SnippetAction.PEEK) {
            mUsedSnippet = null;
            mUsedText = null;
            // forget the used snippet
        }
    }

    private void snippet(Subcommand<SnippetAction> subcmd) {
        String label = subcmd.instruction;
        if (label != null) {
            String lcLabel = label.toLowerCase();
            if (mSnippetsFragment.contains(lcLabel)) {
                if (label.equals(mUsedSnippet)) {
                    // todo: you could change the submit icon to indicate this behavior. it would
                    //  require monitoring text changes and updating the icon each time the user
                    //  types. if the instruction is the already-copied text, set the close icon.
                    //  otherwise, set/keep the copy icon. you could even query the system clipboard
                    //  instead for this check, and listen for copy events that change what the
                    //  behavior & icon should be.
                    succeed();
                    return;
                }

                mUsedSnippet = label;
                snippet(label, lcLabel, subcmd.action);
            } else {
                failMessage(str(R.string.error_no_snippet, label));
            }
        } else if (mSnippetsFragment.isEmpty()) {
            failMessage(R.string.error_no_snippets);
        } else {
            mSnippetsFragment.prime(subcmd.action);
        }
        // TODO: respect the user's letter case for the labels while retaining case-insensitivity
    }

    private void snippet(String label, String lcLabel, SnippetAction action) {
        switch (action) {
        case PEEK -> mSnippetsFragment.peek(lcLabel);
        case GET -> mSnippetsFragment.get(lcLabel);
        case POP -> mSnippetsFragment.pop(label, lcLabel);
        case REMOVE -> {
            mSnippetsFragment.remove(label, lcLabel);
            give(act -> {});
        }}
    }

    public void remove(String label, String lcLabel) {
        mSnippetsFragment.remove(label, lcLabel);
        give(act -> {});
    }

    @Override
    protected void runWithData(String text) {
        if (mSnippetsFragment.isEmpty()) {
            failMessage(R.string.error_no_snippets);
            return;
        }

        mSnippetsFragment.prime(SnippetAction.ADD);
    }

    @Override
    protected void runWithData(String label, String text) {
        mAction = SnippetAction.ADD;

        if (label.equals(mUsedSnippet) && text.equals(mUsedText)) {
            // todo: you could change the submit icon to indicate this behavior. it would
            //  require monitoring text changes and updating the icon each time the user types.
            //  if the instruction is the already-copied text, set the close icon. otherwise,
            //  set/keep the copy icon. you could even query the system clipboard instead for
            //  this check, and listen for copy events that change what the behavior & icon
            //  should be.
            succeed();
            return;
        }

        mSnippetsFragment.tryAdd(label.toLowerCase(), text);

        mUsedSnippet = label;
        mUsedText = text;
    }

}
