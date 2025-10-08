package net.emilla.app;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.command.app.AospContacts;
import net.emilla.command.app.Discord;
import net.emilla.command.app.Firefox;
import net.emilla.command.app.Github;
import net.emilla.command.app.Markor;
import net.emilla.command.app.Newpipe;
import net.emilla.command.app.Outlook;
import net.emilla.command.app.Signal;
import net.emilla.command.app.Tasker;
import net.emilla.command.app.Tor;
import net.emilla.command.app.Tubular;
import net.emilla.command.app.Youtube;

public final class AppProperties {

    /// Metadata for an app entry containing resources and behavior rules. If an app-command isn't
    /// registered for the app, this method returns the basic properties to treat generic apps with.
    ///
    /// @param pkg package name of the app.
    /// @param cls class name of the app.
    /// @return the app properties associated with the given app.
    public static AppProperties of(String pkg, String cls) {
        return switch (pkg) {
            case AospContacts.PKG -> AospContacts.meta();
            case Markor.PKG -> Markor.meta(cls);
            case Firefox.PKG -> Firefox.meta();
            case Tor.PKG -> Tor.meta();
            case Signal.PKG -> Signal.meta();
            case Newpipe.PKG -> Newpipe.meta();
            case Tubular.PKG -> Tubular.meta();
            case Tasker.PKG -> Tasker.meta();
            case Github.PKG -> Github.meta();
            case Youtube.PKG -> Youtube.meta();
            case Discord.PKG -> Discord.meta();
            case Outlook.PKG -> Outlook.meta();
            default -> unspecified();
        };
    }

    /// Unspecific properties for any app without hard-coded metadata.
    ///
    /// @return the basic app properties to treat generic apps with.
    private static AppProperties unspecified() {
        return new AppProperties(0, 0, 0, false);
    }

    /// Unspecific properties for FOSS apps without hard-coded metadata.
    ///
    /// @return the basic app properties to treat generic apps with.
    public static AppProperties unspecifiedFree() {
        return new AppProperties(0, 0, 0, true);
    }

    /// Typical app properties without action suppression.
    ///
    /// @param aliases the app's default set of aliases.
    /// @param summary the app's summary description.
    /// @return app properties with the given attributes.
    public static AppProperties ordinary(@ArrayRes int aliases, @StringRes int summary) {
        return new AppProperties(aliases, summary, 0, false);
    }

    /// Typical FOSS app properties without action suppression.
    ///
    /// @param aliases the app's default set of aliases.
    /// @param summary the app's summary description.
    /// @return app properties with the given attributes.
    public static AppProperties ordinaryFree(@ArrayRes int aliases, @StringRes int summary) {
        return new AppProperties(aliases, summary, 0, true);
    }

    /// App properties with total action suppression. Use this if an app has secondary launchers.
    ///
    /// @return generic app properties with all sub-actions suppressed.
    public static AppProperties suppressive() {
        return new AppProperties(0, 0, ~0, false);
    }

    /// App properties with action suppression. Use this if the app has obnoxious, redundant, or
    /// broken intent filters.
    ///
    /// @param aliases the app's default set of aliases.
    /// @param summary the app's summary description.
    /// @param suppressedActions bit-flags of [AppActions] the app command shouldn't use.
    /// @return app properties with the given attributes.
    public static AppProperties suppressive(
        @ArrayRes int aliases,
        @StringRes int summary,
        int suppressedActions
    ) {
        return new AppProperties(aliases, summary, suppressedActions, false);
    }

    /// FOSS app properties with total action suppression. Use this if an app has secondary
    /// launchers.
    ///
    /// @return generic app properties with all sub-actions suppressed.
    public static AppProperties suppressiveFree() {
        return new AppProperties(0, 0, ~0, true);
    }

    /// FOSS app properties with action suppression. Use this if the app has obnoxious, redundant,
    /// or broken intent filters.
    ///
    /// @param aliases the app's default set of aliases.
    /// @param summary the app's summary description.
    /// @param suppressedActions bit-flags of [AppActions] the app command shouldn't use.
    /// @return app properties with the given attributes.
    public static AppProperties suppressiveFree(
        @ArrayRes int aliases,
        @StringRes int summary,
        int suppressedActions
    ) {
        return new AppProperties(aliases, summary, suppressedActions, true);
    }

    /// The app's default set of aliases.
    @ArrayRes
    public final int aliases;
    /// The app's summary description.
    @StringRes
    public final int summary;
    /// A bit-mask excluding [AppActions] this app's command shouldn't use.
    public final int actionMask;
    /// Whether the app is known to be open source.
    public final boolean isFoss;

    private AppProperties(
        @ArrayRes int aliases,
        @StringRes int summary,
        int suppressedActions,
        boolean isFoss
    ) {
        this.aliases = aliases;
        this.summary = summary;
        this.actionMask = ~suppressedActions;
        this.isFoss = isFoss;
    }
}
