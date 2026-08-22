package net.emilla.command.core;

import android.view.inputmethod.EditorInfo;

import net.emilla.Feedback;
import net.emilla.R;
import net.emilla.action.Gadget;
import net.emilla.action.Widget;
import net.emilla.annotation.internal;
import net.emilla.command.EmillaCommand;
import net.emilla.gadget.DoubleSubmit;
import net.emilla.util.Clipboard;
import net.emilla.wadget.ActionSurface;

final class Copy extends EmillaCommand {
    private final DoubleSubmit mDoubleSubmit = new DoubleSubmit();

    @internal Copy(ActionSurface surface) {
        super(surface, CoreEntry.COPY, EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected Widget[] widgets() {
        return new Widget[] {
            mDoubleSubmit,
        };
    }

    @Override
    protected Gadget[] gadgets() {
        return new Gadget[] {
            mDoubleSubmit,
        };
    }

    @Override
    protected Feedback run(ActionSurface surface) {
        return Feedback.fail(R.string.error_unfinished_feature);
        // Todo: copy a file?
    }

    @Override
    protected Feedback run(ActionSurface surface, String text) {
        return mDoubleSubmit.tap(surface, surf -> {
            Clipboard.copy(surf.getContext(), text);
            return Feedback.selectInstruction();
        });
    }
}
