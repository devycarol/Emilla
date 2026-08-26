package net.emilla.command;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.emilla.Feedback;
import net.emilla.action.Gadget;
import net.emilla.action.Widget;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.annotation.open;
import net.emilla.command.app.AppEntry;
import net.emilla.command.app.AppYielder;
import net.emilla.command.core.CoreEntry;
import net.emilla.config.Aliases;
import net.emilla.config.SettingVals;
import net.emilla.datafield.DataDirective;
import net.emilla.datafield.DataField;
import net.emilla.datafield.Subcommands;
import net.emilla.wadget.ActionSurface;
import net.emilla.widget.ActionIcon;

import java.util.Objects;
import java.util.Set;

public abstract class EmillaCommand {
    public static CommandMap map(
        SharedPreferences prefs,
        Resources res,
        PackageManager pm,
        AppEntry[] apps
    ) {
        Aliases.reformatCoresIfNecessary(prefs);

        var map = new CommandMap(res, SettingVals.defaultCommand(prefs));

        for (var coreEntry : CoreEntry.values()) {
            if (!coreEntry.isEnabled(pm, prefs)) {
                continue;
            }

            CommandYielder yielder = coreEntry.yielder();
            map.put(coreEntry.name(res), yielder);

            Set<String> aliases = coreEntry.aliases(prefs, res);
            if (aliases == null) {
                continue;
            }

            for (String alias : aliases) {
                map.put(alias, yielder);
            }
        }

        for (AppEntry app : apps) {
            if (!app.isEnabled(prefs)) {
                continue;
            }

            // todo: edge case where a mapped app is uninstalled during the activity lifecycle
            AppYielder yielder = app.yielder();
            map.put(app.displayName, yielder);

            Set<String> aliases = app.aliases(prefs, res);
            if (aliases == null) {
                continue;
            }

            for (String alias : aliases) {
                map.put(alias, yielder);
            }
        }

//        Set<String> customs = SettingVals.customCommands(prefs);
        // Todo: custom commands with preset instructions.
        // Todo: the previous string-set approach means it's anyone's guess whether custom aliases
        //  can *successfully* map to one another at mapping time, and it guarantees they can't be
        //  reciprocally used. that's good for now since that'd be infinite recursion, but this
        //  should be borne in mind when creating a more robust custom command system.
//        for (String customEntry : customs) {
//            String[] split = Patterns.TRIMMING_CSV.split(customEntry);
//            int last = split.length - 1;
//            for (int i = 0; i < last; ++i) {
//                map.putCustom(split[i], split[last]);
//            }
//        }

        return map;
    }

    private final Params mParams;

    public final String name;
    @StringRes
    public final int summary;
    @StringRes
    public final int manual;
    private final ImeAction mImeAction;

    @Nullable
    private String mInstruction = null;
    private boolean mActive = false;

    protected EmillaCommand(
        ActionSurface surface,
        Params params,
        @StringRes int summary,
        @StringRes int manual,
        ImeAction imeAction
    ) {
        mParams = params;
        this.name = params.name(surface.getResources());
        this.summary = summary;
        this.manual = manual;
        mImeAction = imeAction;
    }

    protected EmillaCommand(ActionSurface surface, CoreEntry coreEntry) {
        this(
            surface,
            coreEntry,
            coreEntry.summary,
            coreEntry.manual,
            coreEntry.imeAction
        );
    }

    protected EmillaCommand(ActionSurface surface, AppEntry appEntry) {
        this(
            surface,
            appEntry,
            appEntry.summary(),
            appEntry.actions.manual(),
            appEntry.actions.imeAction()
        );
    }

    public final CharSequence title(Resources res) {
        return mParams.title(res);
    }

    public final ActionIcon actionIcon(Context ctx) {
        return mParams.actionIcon(ctx);
    }

    public int imeAction() {
        return switch (dataDirective()) {
            case null -> mImeAction.id;
            // todo: also include the command's ImeAction in the "IME options"
            case DataField __ -> EditorInfo.IME_ACTION_NEXT;
            case Subcommands<?> subcommands -> subcommands.imeAction().id;
        };
    }

    public int dataHint() {
        return switch (dataDirective()) {
            case null -> 0;
            case DataField(@StringRes int hint) -> hint;
            case Subcommands<?> subcommands -> subcommands.dataHint();
        };
    }

    @internal final void instruct(
        ActionSurface surface,
        @Nullable String instruction
    ) {
        if (!Objects.equals(mInstruction, instruction)) {
            // we don't assume this is true because input editor bugs may cause
            // onTextChanged() to be called repeatedly for the same text.
            mInstruction = instruction;
            if (mActive) {
                onInstruct(surface, instruction);
            }
        }
    }

    public final void load(AssistActivity act) {
        Widget[] widgets = widgets();
        if (widgets != null) {
            for (Widget widget : widgets) {
                widget.load(act);
            }
        }
        onInstruct(act, mInstruction);
        mActive = true;
    }

    public final void unload(AssistActivity act) {
        Widget[] widgets = widgets();
        if (widgets != null) {
            for (Widget widget : widgets) {
                widget.unload(act);
            }
        }
        mActive = false;
    }

    private void onInstruct(
        ActionSurface surface,
        @Nullable String instruction
    ) {
        Gadget[] gadgets = gadgets();
        if (gadgets != null) {
            for (Gadget gadget : gadgets) {
                gadget.instruct(surface, instruction);
            }
        }
    }

    @Nullable
    protected final String instruction() {
        return mInstruction;
    }

    public final Feedback execute(ActionSurface surface) {
        return mInstruction != null
            ? run(surface, mInstruction)
            : run(surface)
        ;
    }

    /// Runs the command.
    protected abstract Feedback run(ActionSurface surface);
    /// Runs the command with instruction.
    ///
    /// @param instruction is provided after in the command field after the command's name. It's
    /// always space-trimmed and should remain as such.
    protected abstract Feedback run(ActionSurface surface, String instruction);

    @Nullable
    public @open DataDirective dataDirective() {
        return null;
    }

    @Nullable
    protected @open Widget[] widgets() {
        return null;
    }

    @Nullable
    protected @open Gadget[] gadgets() {
        return null;
    }
}
