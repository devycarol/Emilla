package net.emilla.command.core;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.command.ActionMap;
import net.emilla.command.Subcommand;
import net.emilla.run.CopyGift;
import net.emilla.settings.Aliases;
import net.emilla.settings.SettingVals;
import net.emilla.util.Dialogs;

import java.util.Set;

public final class Snippets extends CoreDataCommand {

    public static final String ENTRY = "snippets";
    @StringRes
    public static final int NAME = R.string.command_snippets;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_snippets;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static final Set<String> DFLT_SNIPPETS = Set.of();

    public static Yielder yielder() {
        return new Yielder(true, Snippets::new, ENTRY, NAME, ALIASES);
    }

    private enum Action {
        PEEK,
        GET,
        POP,
        REMOVE,
        ADD
    }

    private ActionMap<Action> mActionMap;
    private Action mAction = Action.GET;
    private Set<String> mSnippetNames;
    private String mUsedSnippet;
    private String mUsedText;

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

        if (mActionMap == null) {
            mActionMap = new ActionMap<>(Action.GET);

            mActionMap.put(resources, Action.PEEK, R.array.subcmd_snippet_peek, true);
            mActionMap.put(resources, Action.GET, R.array.subcmd_snippet_get, true);
            mActionMap.put(resources, Action.POP, R.array.subcmd_snippet_pop, true);
            mActionMap.put(resources, Action.REMOVE, R.array.subcmd_snippet_remove, true);
        }

        if (mSnippetNames == null) mSnippetNames = SettingVals.snippets(prefs());
    }

    @Override
    protected void onClean() {
        super.onClean();
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
    protected void run(@NonNull String label) {
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
        }
    }

    private void snippet(Subcommand<Action> subcmd) {
        String label = subcmd.instruction();
        if (label != null) {
            var lcLabel = label.toLowerCase();
            if (mSnippetNames.contains(lcLabel)) {
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
        } else if (mSnippetNames.isEmpty()) failMessage(R.string.error_no_snippets);
        else {
            var items = mSnippetNames.toArray(new String[0]);
            offerDialog(Dialogs.list(activity, NAME, items,
                    (dlg, which) -> snippet(items[which], items[which], subcmd.action())));
            // TODO: use the case that the user used for the labels while
            //  - must retain case-insensitivity
            //  - would require a 'rename' subcmd
            // Todo: replace with dynamic list widget
        }
    }

    private void snippet(@NonNull String label, @NonNull String lcLabel, Action action) {
        switch (action) {
        case PEEK -> giveMessage(SettingVals.snippet(prefs(), lcLabel));
        case GET -> {
            String snippet = SettingVals.snippet(prefs(), lcLabel);
            give(new CopyGift(activity, snippet));
        }
        case POP -> {
            String snippet = SettingVals.snippet(prefs(), lcLabel);
            give(new CopyGift(activity, snippet));
            mSnippetNames = SettingVals.removeSnippet(prefs(), lcLabel);
            toast(str(R.string.toast_snippet_deleted, label));
        }
        case REMOVE -> {
            mSnippetNames = SettingVals.removeSnippet(prefs(), lcLabel);
            toast(str(R.string.toast_snippet_deleted, label));
            give(() -> {});
        }}
    }

    @Override
    protected void runWithData(@NonNull String text) {
        if (mSnippetNames.isEmpty()) {
            failMessage(R.string.error_no_snippets);
            return;
        }

        var items = mSnippetNames.toArray(new String[0]);
        offerDialog(Dialogs.list(activity, R.string.dialog_overwrite_snippet, items,
                (dlg, which) -> addSnippet(items[which], text)));
    }

    @Override
    protected void runWithData(@NonNull String label, @NonNull String text) {
        mAction = Action.ADD;

        var lcLabel = label.toLowerCase();
        if (mSnippetNames.contains(lcLabel)) {
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

            mUsedSnippet = label;
            mUsedText = text;

            var msg = str(R.string.dlg_msg_overwrite_snippet, label);
            offerDialog(Dialogs.dual(activity, R.string.dialog_overwrite_snippet, msg,
                    R.string.overwrite, (dlg, which) -> addSnippet(lcLabel, text)));
            return;
        }

        mUsedSnippet = label;
        mUsedText = text;

        addSnippet(lcLabel, text);
    }

    private void addSnippet(String label, String text) {
        mSnippetNames = SettingVals.addSnippet(prefs(), label, text);
        give(() -> toast(str(R.string.toast_saved)));
    }
}
