package net.emilla.command.core;

import androidx.annotation.ArrayRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.action.box.SnippetsFragment;
import net.emilla.activity.AssistActivity;
import net.emilla.command.ActionMap;
import net.emilla.command.Subcommand;
import net.emilla.settings.Aliases;

import java.util.Set;

public final class Snippets extends CoreDataCommand {

    public static final String ENTRY = "snippets";
    @StringRes
    public static final int NAME = R.string.command_snippets;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_snippets;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static Yielder yielder() {
        return new Yielder(true, Snippets::new, ENTRY, NAME, ALIASES);
    }

    public enum Action {
        PEEK, GET, POP, REMOVE, ADD
        // Todo: 'rename' action
    }

    public static final Set<String> DFLT_SNIPPETS = Set.of();

    private SnippetsFragment mSnippetsFragment;

    private ActionMap<Action> mActionMap;
    private Action mAction = Action.GET;
    @Nullable
    private String mUsedSnippet, mUsedText;

    public Snippets(AssistActivity act) {
        super(act, NAME,
              R.string.instruction_name_label,
              R.drawable.ic_snippets,
              R.string.summary_snippets,
              R.string.manual_snippets,
              R.string.data_hint_text);
    }

    @Override
    protected void onInit() {
        super.onInit();

        if (mSnippetsFragment == null) mSnippetsFragment = SnippetsFragment.newInstance();
        activity.giveActionBox(mSnippetsFragment);

        if (mActionMap == null) {
            mActionMap = new ActionMap<>(Action.GET);

            mActionMap.put(resources, Action.PEEK, R.array.subcmd_snippet_peek, true);
            mActionMap.put(resources, Action.GET, R.array.subcmd_snippet_get, true);
            mActionMap.put(resources, Action.POP, R.array.subcmd_snippet_pop, true);
            mActionMap.put(resources, Action.REMOVE, R.array.subcmd_snippet_remove, true);
        }
    }

    @Override
    protected void onClean() {
        super.onClean();

        activity.removeActionBox(mSnippetsFragment);
        mSnippetsFragment = null;

        mUsedSnippet = null;
        mUsedText = null;
        // forget the used snippet
    }

    @Override
    protected void run() {
        refreshState(Action.GET);
        snippet(new Subcommand<>(Action.GET, null));
    }

    @Override
    protected void run(String label) {
        Subcommand<Action> subcmd = mActionMap.get(label);
        refreshState(subcmd.action());
        snippet(subcmd);
    }

    private void refreshState(Action action) {
        if (mAction != action) {
            mAction = action;
            mUsedSnippet = null;
            mUsedText = null;
            // forget the used snippet
        } else if (action == Action.PEEK) {
            mUsedSnippet = null;
            mUsedText = null;
            // forget the used snippet
        }
    }

    private void snippet(Subcommand<Action> subcmd) {
        String label = subcmd.instruction();
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
                snippet(label, lcLabel, subcmd.action());
            } else failMessage(str(R.string.error_no_snippet, label));
        } else if (mSnippetsFragment.isEmpty()) failMessage(R.string.error_no_snippets);
        else mSnippetsFragment.prime(subcmd.action());
        // TODO: respect the user's letter case for the labels while retaining case-insensitivity
    }

    private void snippet(String label, String lcLabel, Action action) {
        switch (action) {
        case PEEK -> mSnippetsFragment.peek(lcLabel);
        case GET -> mSnippetsFragment.get(lcLabel);
        case POP -> mSnippetsFragment.pop(label, lcLabel);
        case REMOVE -> {
            mSnippetsFragment.remove(label, lcLabel);
            give(() -> {});
        }}
    }

    public void remove(String label, String lcLabel) {
        mSnippetsFragment.remove(label, lcLabel);
        give(() -> {});
    }

    @Override
    protected void runWithData(String text) {
        if (mSnippetsFragment.isEmpty()) {
            failMessage(R.string.error_no_snippets);
            return;
        }

        mSnippetsFragment.prime(Action.ADD);
    }

    @Override
    protected void runWithData(String label, String text) {
        mAction = Action.ADD;

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
