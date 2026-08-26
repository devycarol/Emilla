package net.emilla.command.core;

import androidx.annotation.Nullable;

import net.emilla.Feedback;
import net.emilla.R;
import net.emilla.action.Gadget;
import net.emilla.action.Widget;
import net.emilla.action.box.SnippetsFragment;
import net.emilla.annotation.internal;
import net.emilla.command.EmillaCommand;
import net.emilla.command.ImeAction;
import net.emilla.datafield.DataDirective;
import net.emilla.datafield.DataSubcommand;
import net.emilla.datafield.IdSubcommand;
import net.emilla.datafield.Subcommands;
import net.emilla.wadget.ActionSurface;

final class Snippets extends EmillaCommand {
    private sealed interface SnippetAction permits Id, Edit {
    }

    private enum Id implements SnippetAction {
        COPY,
        CUT,
        VIEW,
        // Todo: 'rename'
        DELETE,
    }

    private record Edit(@Nullable String text) implements SnippetAction {
    }

    private final Subcommands<SnippetAction> mSubcommands = new Subcommands<>(
        new IdSubcommand<>(Id.COPY, R.drawable.ic_copy, ImeAction.DO),
        new DataSubcommand<>(
            Edit::new,
            R.drawable.ic_edit,
            ImeAction.DO,
            R.string.data_hint_text
        ),
        new IdSubcommand<>(Id.CUT, R.drawable.ic_cut, ImeAction.DO),
        new IdSubcommand<>(Id.VIEW, R.drawable.ic_view, ImeAction.DO),
        new IdSubcommand<>(Id.DELETE, R.drawable.ic_delete, ImeAction.DO)
    );
    private final SnippetsFragment mSnippetsFragment
        = SnippetsFragment.newInstance()
    ;

    @internal Snippets(ActionSurface surface) {
        super(surface, CoreEntry.SNIPPETS, ImeAction.DO);
    }

    @Override
    public DataDirective dataDirective() {
        return mSubcommands;
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

    @Override
    protected Feedback run(ActionSurface surface) {
        return Feedback.pend();
    }

    @Override
    protected Feedback run(ActionSurface surface, String label) {
        SnippetAction subcommand = mSubcommands.get(surface);
        return switch (subcommand) {
            case Id id -> switch (id) {
                // Todo: watch for the stupid error requiring this nested switch
                //  to be fixed
                case VIEW -> mSnippetsFragment.peek(label);
                case COPY -> mSnippetsFragment.copy(label);
                case CUT -> mSnippetsFragment.pop(label);
                case DELETE -> mSnippetsFragment.remove(label);
            };
            case Edit(@Nullable String text) -> text != null
                ? mSnippetsFragment.add(label, text)
                : Feedback.pend()
            ;
        };
    }
}
