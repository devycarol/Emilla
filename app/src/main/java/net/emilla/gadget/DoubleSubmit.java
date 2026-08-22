package net.emilla.gadget;

import androidx.annotation.Nullable;

import net.emilla.Feedback;
import net.emilla.R;
import net.emilla.action.Gadget;
import net.emilla.action.Widget;
import net.emilla.activity.AssistActivity;
import net.emilla.wadget.ActionSurface;
import net.emilla.widget.SymbolIcon;

import java.util.function.Function;

public final class DoubleSubmit implements Widget, Gadget {
    private boolean mIs = false;

    public Feedback tap(
        ActionSurface surface,
        Function<ActionSurface, Feedback> function
    ) {
        if (mIs) {
            return Feedback.succeed();
        }

        mIs = true;
        surface.setSubmitIcon(new SymbolIcon(R.drawable.ic_close));
        return function.apply(surface);
    }

    @Override
    public void load(AssistActivity act) {
    }

    @Override
    public void instruct(ActionSurface surface, @Nullable String instruction) {
        if (mIs) {
            surface.resetSubmitIcon();
            mIs = false;
        }
    }

    @Override
    public void unload(AssistActivity act) {
        mIs = false;
    }
}
