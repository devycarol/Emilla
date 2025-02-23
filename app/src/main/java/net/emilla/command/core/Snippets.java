package net.emilla.command.core;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.run.CopyGift;
import net.emilla.settings.Aliases;
import net.emilla.settings.SettingVals;
import net.emilla.util.Dialogs;
import net.emilla.util.Strings;

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

    private static final byte
            VIEW = -1, // todo: adding this requires changing the comparison logic in snippetAction().
            GET = 0,
            POP = 1,
            REMOVE = 2,
            ADD = 3;

    private byte mAction = GET;
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
        if (mAction != GET) {
            mAction = GET;
            mUsedSnippet = null;
            mUsedText = null;
            // forget the used snippet
        }
        snippet(null);
    }

    @Override
    protected void run(@NonNull String label) {
        snippet(extractAction(label));
    }

    private String extractAction(String label) {
        var lcLabel = label.toLowerCase();
        byte action;
        if (lcLabel.startsWith("get")) {
            action = GET;
            label = Strings.subTrimToNull(label, 3);
        } else if (lcLabel.startsWith("copy")) {
            action = GET;
            label = Strings.subTrimToNull(label, 4);
        } else if (lcLabel.startsWith("cp")) {
            action = GET;
            label = Strings.subTrimToNull(label, 2);
        } else if (lcLabel.startsWith("pop") || lcLabel.startsWith("cut")) {
            action = POP;
            label = Strings.subTrimToNull(label, 3);
        } else if (lcLabel.startsWith("remove") || lcLabel.startsWith("delete")) {
            action = REMOVE;
            label = Strings.subTrimToNull(label, 6);
        } else if (lcLabel.startsWith("rm")) {
            action = REMOVE;
            label = Strings.subTrimToNull(label, 2);
        } else if (lcLabel.startsWith("del")) {
            action = REMOVE;
            label = Strings.subTrimToNull(label, 3);
        } else action = GET;
        // TODO LANG: use TrieMap for all subcommands

        if (mAction != action) {
            mAction = action;
            mUsedSnippet = null;
            mUsedText = null;
            // forget the used snippet
        }

        return label;
    }

    private void snippet(@Nullable String label) {
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
                snippetAction(label, lcLabel);
            } else failMessage(str(R.string.error_no_snippet, label));
        } else if (mSnippetNames.isEmpty()) failMessage(R.string.error_no_snippets);
        else {
            var items = mSnippetNames.toArray(new String[0]);
            offerDialog(Dialogs.list(activity, NAME, items,
                    (dlg, which) -> snippetAction(items[which], items[which])));
            // TODO: use the case that the user used for the labels while
            //  - must retain case-insensitivity
            //  - would require a 'rename' subcmd
            // Todo: replace with dynamic list widget
        }
    }

    private void snippetAction(@NonNull String label, @NonNull String lcLabel) {
        if (mAction <= POP) {
            String snippet = SettingVals.snippet(prefs(), lcLabel);
            give(new CopyGift(activity, snippet));
        }
        if (mAction >= POP) {
            mSnippetNames = SettingVals.removeSnippet(prefs(), lcLabel);
            toast(str(R.string.toast_snippet_deleted, label));
            if (mAction == REMOVE) give(() -> {});
        }
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
        mAction = ADD;

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
        giveText(str(R.string.toast_saved), false);
    }
}
