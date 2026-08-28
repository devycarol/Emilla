package net.emilla.command.app;

import android.app.SearchManager;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.emilla.Feedback;
import net.emilla.R;
import net.emilla.annotation.internal;
import net.emilla.command.ImeAction;
import net.emilla.datafield.DataDirective;
import net.emilla.datafield.DataField;
import net.emilla.datafield.DataSubcommand;
import net.emilla.datafield.IdSubcommand;
import net.emilla.datafield.Subcommands;
import net.emilla.util.Intents;
import net.emilla.wadget.ActionSurface;

final class AppInstruct extends AppCommand {
    private sealed interface AppAction permits Id, Send {
    }

    private enum Id implements AppAction {
        SEARCH,
    }

    private record Send(@Nullable String text) implements AppAction {
    }

    private final DataDirective mDataDirective;

    @internal AppInstruct(ActionSurface surface, AppEntry appEntry) {
        this(surface, appEntry, R.string.data_hint_text);
    }

    @internal AppInstruct(
        ActionSurface surface,
        AppEntry appEntry,
        @StringRes int sendDataHint
    ) {
        super(surface, appEntry);
        if (appEntry.actions.hasSend()) {
            if (appEntry.actions.hasSearch()) {
                mDataDirective = new Subcommands<AppAction>(
                    new DataSubcommand<>(
                        Send::new,
                        R.drawable.ic_send,
                        ImeAction.SEND,
                        sendDataHint
                    ),
                    new IdSubcommand<>(
                        Id.SEARCH,
                        R.drawable.ic_search,
                        ImeAction.SEARCH
                    )
                );
            } else {
                mDataDirective = new DataField(R.string.data_hint_message_cont);
            }
        } else {
            mDataDirective = null;
        }
    }

    @Override
    protected Feedback run(ActionSurface surface, String instruction) {
        return switch (mDataDirective) {
            case null -> appSearch(instruction);
            case DataField __ -> send(instruction, surface.dataText());
            case Subcommands<?> subcommands
                -> switch ((AppAction) subcommands.get(surface))
            {
                case Send(@Nullable String text)
                    -> send(instruction, text)
                ;
                // Todo: watch for the stupid error requiring this nested switch
                //  to be fixed
                case Id __ -> appSearch(instruction);
            };
        };
    }

    private Feedback send(String text, @Nullable String cont) {
        if (cont != null) {
            text += cont;
        }
        return send(text);
    }

    private Feedback appSearch(String query) {
        return Feedback.succeed(Intents.searchToApp(this.appEntry.pkg)
            .putExtra(SearchManager.QUERY, query)
        );
    }
}
