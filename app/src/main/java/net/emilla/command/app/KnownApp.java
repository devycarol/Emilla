package net.emilla.command.app;

import androidx.annotation.ArrayRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.annotation.internal;
import net.emilla.annotation.open;
import net.emilla.util.TaskerIntent;
import net.emilla.wadget.ActionSurface;

public enum KnownApp {
    AOSP_CONTACTS(R.string.instruction_contact, R.array.aliases_aosp_contacts, R.string.summary_app_aosp_contacts),
    MARKOR(R.string.instruction_text, R.array.aliases_markor, R.string.summary_note, R.string.data_hint_text),
    FIREFOX(R.string.instruction_web, R.array.aliases_firefox, R.string.summary_web, AppActions.FLAGS_SEND_TEXT, null),
    // 'send' is redundant for Firefox, it just searches
    TOR(0, R.array.aliases_tor, R.string.summary_web, AppActions.FLAGS_SEND_TEXT | AppActions.FLAG_SEARCH, null) {
        // search/send intents are broken, therefore no instruction
        @Override
        public AppCommand make(ActionSurface surface, AppEntry appEntry) {
            return new AppCommand(surface, appEntry);
        }
    },
    SIGNAL(R.string.instruction_message, R.array.aliases_signal, R.string.summary_messaging, R.string.data_hint_message_cont),
    NEWPIPE(R.string.instruction_video, R.array.aliases_newpipe, R.string.summary_video) {
        @Override
        public AppCommand make(ActionSurface surface, AppEntry appEntry) {
            return new AppSend(surface, appEntry);
            // TODO DONT: "video title"
        }
    },
    TUBULAR(R.string.instruction_video, R.array.aliases_tubular, R.string.summary_video) {
        @Override
        public AppCommand make(ActionSurface surface, AppEntry appEntry) {
            return new AppSend(surface, appEntry);
            // TODO DONT: "video title"
        }
    },
    TASKER(R.string.instruction_app_tasker, R.array.aliases_tasker, R.string.summary_app_tasker, ~AppActions.FLAG_TASKER, null) {
        // because it's an automation app, Tasker has a lot of auxiliary intent
        // filters. we want to suppress them all and only use our own handling.
        @Override
        public AppCommand make(ActionSurface surface, AppEntry appEntry) {
            return new Tasker(surface, appEntry);
        }
    },
    GITHUB(R.string.instruction_app_issue, R.array.aliases_github, R.string.summary_issues, R.string.data_hint_issue),
    YOUTUBE(R.string.instruction_video, R.array.aliases_youtube, R.string.summary_video),
    // Todo: instantly pull up bookmarked videos, specialized search for
    //  channels, playlists, etc. I assume the G assistant has similar
    //  functionality. If requires internet could use bookmarks at the very
    //  least. Also, this command is broken when a video is playing.
    DISCORD(R.string.instruction_message, R.array.aliases_discord, R.string.summary_messaging, AppActions.FLAG_SEND_MULTILINE, null) {
        @Override
        public AppCommand make(ActionSurface surface, AppEntry appEntry) {
            return new AppSend(surface, appEntry);
        }
    },
    OUTLOOK(R.string.instruction_app_email, R.array.aliases_outlook, R.string.summary_email, R.string.data_hint_email),
;
    private static final String PKG_MARKOR = "net.gsantner.markor";

    @StringRes
    @internal final int instruction;
    @ArrayRes
    public final int aliases;
    @StringRes
    @internal final int summary;
    @StringRes
    private final int mSendDataHint;
    @internal final int actionMask;

    KnownApp(
        @StringRes int instruction,
        @ArrayRes int aliases,
        @StringRes int summary
    ) {
        this(instruction, aliases, summary, 0, 0);
    }

    KnownApp(
        @StringRes int instruction,
        @ArrayRes int aliases,
        @StringRes int summary,
        @StringRes int sendDataHint
    ) {
        this(instruction, aliases, summary, sendDataHint, 0);
    }

    KnownApp(
        @StringRes int instruction,
        @ArrayRes int aliases,
        @StringRes int summary,
        int suppressedActions,
        @Nullable Void bruh
    ) {
        this(instruction, aliases, summary, 0, suppressedActions);
    }

    KnownApp(
        @StringRes int instruction,
        @ArrayRes int aliases,
        @StringRes int summary,
        @StringRes int sendDataHint,
        int suppressedActions
    ) {
        this.instruction = instruction;
        this.aliases = aliases;
        this.summary = summary;
        mSendDataHint = sendDataHint;
        this.actionMask = ~suppressedActions;
    }

    public @open AppCommand make(ActionSurface surface, AppEntry appEntry) {
        return mSendDataHint != 0
            ? new AppInstruct(surface, appEntry, mSendDataHint)
            : new AppInstruct(surface, appEntry)
        ;
    }

    @Nullable
    public static KnownApp of(String pkg, String cls) {
        return switch (pkg) {
            case "com.android.contacts" -> AOSP_CONTACTS;
            case PKG_MARKOR -> cls.equals(PKG_MARKOR + ".activity.MainActivity")
                // Markor can have multiple launchers, only the main should have
                // special properties.
                ? MARKOR
                : null
            ;
            case "org.mozilla.firefox" -> FIREFOX;
            case "org.torproject.torbrowser" -> TOR;
            case "org.thoughtcrime.securesms" -> SIGNAL;
            case "org.schabi.newpipe" -> NEWPIPE;
            case "org.polymorphicshade.tubular" -> TUBULAR;
            case TaskerIntent.TASKER_PACKAGE_MARKET -> TASKER;
            case "com.github.android" -> GITHUB;
            case "com.google.android.youtube" -> YOUTUBE;
            case "com.discord" -> DISCORD;
            case "com.microsoft.office.outlook" -> OUTLOOK;
            default -> null;
        };
    }
}
