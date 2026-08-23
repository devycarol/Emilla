package net.emilla.command.core;

import androidx.annotation.Nullable;

import net.emilla.Feedback;
import net.emilla.R;
import net.emilla.action.Gadget;
import net.emilla.action.Widget;
import net.emilla.action.box.SnippetsFragment;
import net.emilla.annotation.internal;
import net.emilla.command.ActionMap;
import net.emilla.command.EmillaCommand;
import net.emilla.command.ImeAction;
import net.emilla.command.Subcommand;
import net.emilla.datafield.DataDirective;
import net.emilla.datafield.DataField;
import net.emilla.exception.UnreachableError;
import net.emilla.wadget.ActionSurface;

final class Snippets extends EmillaCommand {
    private final SnippetsFragment mSnippetsFragment = SnippetsFragment.newInstance();

    private final ActionMap<SnippetAction> mActionMap;
    private SnippetAction mAction = SnippetAction.GET;

    @internal Snippets(ActionSurface surface) {
        super(surface, CoreEntry.SNIPPETS, ImeAction.DO);
        var res = surface.getResources();
        mActionMap = new ActionMap<SnippetAction>(res, SnippetAction.GET, SnippetAction[]::new);

        mActionMap.put(res, SnippetAction.PEEK, R.array.subcmd_snippet_peek, true);
        mActionMap.put(res, SnippetAction.GET, R.array.subcmd_snippet_get, true);
        mActionMap.put(res, SnippetAction.POP, R.array.subcmd_snippet_pop, true);
        mActionMap.put(res, SnippetAction.REMOVE, R.array.subcmd_snippet_remove, true);
    }

    @Override
    protected DataDirective dataDirective() {
        return new DataField(R.string.data_hint_text);
    }

    @Override
    protected Widget[] widgets() {
        return new Widget[] {
            mSnippetsFragment,
        };
    }

    @Override
    protected Gadget[] gadgets() {
        return new Gadget[] {
            mSnippetsFragment,
        };
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
    protected Feedback run(ActionSurface surface) {
        return Feedback.pend();
    }

    @Override
    protected Feedback run(ActionSurface surface, String label) {
        String text = surface.dataText();
        if (text != null) {
            return mSnippetsFragment.add(label, text);
        }

        label = extractAction(label);
        if (label == null) {
            return run(surface);
        }

        return switch (mAction) {
            case PEEK -> mSnippetsFragment.peek(label);
            case GET -> mSnippetsFragment.copy(label);
            case POP -> mSnippetsFragment.pop(label);
            case REMOVE -> mSnippetsFragment.remove(label);
            case ADD -> throw new UnreachableError();
        };
    }
}
