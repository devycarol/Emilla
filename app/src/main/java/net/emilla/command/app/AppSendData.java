package net.emilla.command.app;

import androidx.annotation.StringRes;

import net.emilla.Feedback;
import net.emilla.R;
import net.emilla.annotation.internal;
import net.emilla.annotation.open;
import net.emilla.command.DataField;
import net.emilla.wadget.ActionSurface;

@open class AppSendData extends AppSend {
    @internal AppSendData(ActionSurface surface, AppEntry appEntry) {
        this(surface, appEntry, R.string.data_hint_text);
    }

    @internal AppSendData(
        ActionSurface surface,
        AppEntry appEntry,
        @StringRes int hint
    ) {
        super(surface, appEntry, new DataField(hint));
    }

    @Override
    protected final Feedback run(ActionSurface surface) {
        String message = surface.dataText();
        return message != null
            ? super.run(surface, message)
            : super.run(surface)
        ;
    }

    @Override
    protected final Feedback run(ActionSurface surface, String message) {
        String cont = surface.dataText();
        if (cont != null) {
            message += '\n' + cont;
        }
        return super.run(surface, message);
    }
}
