package net.emilla.command.app;

import androidx.annotation.StringRes;

import net.emilla.Feedback;
import net.emilla.R;
import net.emilla.annotation.internal;
import net.emilla.annotation.open;
import net.emilla.command.ImeAction;
import net.emilla.datafield.DataDirective;
import net.emilla.datafield.DataField;
import net.emilla.wadget.ActionSurface;

@open class AppSendData extends AppSend {
    @StringRes
    private final int mDataHint;

    @internal AppSendData(ActionSurface surface, AppEntry appEntry) {
        this(surface, appEntry, ImeAction.SEND, R.string.data_hint_text);
    }

    @internal AppSendData(
        ActionSurface surface,
        AppEntry appEntry,
        ImeAction imeAction,
        @StringRes int dataHint
    ) {
        super(surface, appEntry, imeAction);
        mDataHint = dataHint;
    }

    @Override
    protected final DataDirective dataDirective() {
        return new DataField(mDataHint);
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
