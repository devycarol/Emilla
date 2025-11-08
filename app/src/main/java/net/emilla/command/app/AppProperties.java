package net.emilla.command.app;

import androidx.annotation.ArrayRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.emilla.R;

public enum AppProperties {
    AOSP_CONTACTS(AospContacts::instance, R.string.instruction_contact, R.array.aliases_aosp_contacts, R.string.summary_app_aosp_contacts, true),
    MARKOR(Markor::instance, R.string.instruction_text, R.array.aliases_markor, R.string.summary_note, true),
    FIREFOX(Firefox::instance, R.string.instruction_web, R.array.aliases_firefox, R.string.summary_web, true, AppActions.FLAGS_SEND_TEXT),
    // 'send' is redundant for Firefox, it just searches
    TOR(Tor::instance, 0, R.array.aliases_tor, R.string.summary_web, true, AppActions.FLAGS_SEND_TEXT | AppActions.FLAG_SEARCH),
    // search/send intents are broken, therefore no instruction
    SIGNAL(Signal::instance, R.string.instruction_message, R.array.aliases_signal, R.string.summary_messaging, true),
    NEWPIPE(NewPipe::instance, R.string.instruction_video, R.array.aliases_newpipe, R.string.summary_video, true),
    TUBULAR(Tubular::instance, R.string.instruction_video, R.array.aliases_tubular, R.string.summary_video, true),
    TASKER(Tasker::new, R.string.instruction_app_tasker, R.array.aliases_tasker, R.string.summary_app_tasker, false, ~AppActions.FLAG_TASKER),
    // because it's an automation app, Tasker has a lot of auxiliary intent filters. we want to
    // suppress them all and only use our own handling.
    GITHUB(GitHub::instance, R.string.instruction_issue, R.array.aliases_github, R.string.summary_issues, false),
    YOUTUBE(YouTube::instance, R.string.instruction_video, R.array.aliases_youtube, R.string.summary_video, false),
    DISCORD(Discord::instance, R.string.instruction_message, R.array.aliases_discord, R.string.summary_messaging, false, AppActions.FLAG_SEND_MULTILINE),
    OUTLOOK(Outlook::instance, R.string.instruction_app_email, R.array.aliases_outlook, R.string.summary_email, false);

    public final AppCommand.Maker maker;
    @StringRes
    public final int instruction;
    @ArrayRes
    public final int aliases;
    @StringRes
    public final int summary;
    public final boolean isFoss;
    public final int actionMask;

    AppProperties(
        AppCommand.Maker maker,
        @StringRes int instruction,
        @ArrayRes int aliases,
        @StringRes int summary,
        boolean isFoss
    ) {
        this(maker, instruction, aliases, summary, isFoss, 0);
    }

    AppProperties(
        AppCommand.Maker maker,
        @StringRes int instruction,
        @ArrayRes int aliases,
        @StringRes int summary,
        boolean isFoss,
        int suppressedActions
    ) {
        this.maker = maker;
        this.instruction = instruction;
        this.aliases = aliases;
        this.summary = summary;
        this.isFoss = isFoss;
        this.actionMask = ~suppressedActions;
    }

    @Nullable
    public static AppProperties of(String pkg, String cls) {
        return switch (pkg) {
            case AospContacts.PKG -> AOSP_CONTACTS;
            case Markor.PKG -> cls.equals(Markor.CLS_MAIN) ? MARKOR : null;
            // Markor can have multiple launchers, only the main should have special properties.
            case Firefox.PKG -> FIREFOX;
            case Tor.PKG -> TOR;
            case Signal.PKG -> SIGNAL;
            case NewPipe.PKG -> NEWPIPE;
            case Tubular.PKG -> TUBULAR;
            case Tasker.PKG -> TASKER;
            case GitHub.PKG -> GITHUB;
            case YouTube.PKG -> YOUTUBE;
            case Discord.PKG -> DISCORD;
            case Outlook.PKG -> OUTLOOK;
            default -> null;
        };
    }

}
