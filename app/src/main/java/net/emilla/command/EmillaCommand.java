package net.emilla.command;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.emilla.Feedback;
import net.emilla.R;
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
import net.emilla.lang.Lang;
import net.emilla.wadget.ActionSurface;

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

    public final Params params;

    public final String name;
    @StringRes
    public final int summary;
    @StringRes
    public final int manual;
    /// The command's "IME action." This determines the soft keyboard's enter key icon. The options
    /// are GO, SEARCH, SEND, DONE, and NEXT. GO is usually a forward arrow, SEARCH is usually a
    /// magnifying glass, SEND is usually a paper airplane, and DONE is usually a checkmark. NEXT is
    /// the 'tab' function and should be used when the data field is available.
    private final int mImeAction;
    // todo: you should be able to long-click the enter key in the command or data field to
    //  submit the command, using an appropriate action icon.
    // requires changing the input method code directly
    // it's also proven cumbersome to get the key icon to actually update to begin with..

    @Nullable
    private String mInstruction = null;
    private boolean mActive = false;

    @StringRes
    public final int dataHint;

    protected EmillaCommand(
        ActionSurface surface,
        Params params,
        @StringRes int summary,
        @StringRes int manual,
        int imeAction
    ) {
        this(surface, params, summary, manual, imeAction, null);
    }

    protected EmillaCommand(
        ActionSurface surface,
        CoreEntry coreEntry,
        int imeAction
    ) {
        this(
            surface,
            coreEntry,
            coreEntry.summary,
            coreEntry.manual,
            imeAction
        );
    }

    protected EmillaCommand(
        ActionSurface surface,
        CoreEntry coreEntry,
        DataField dataField
    ) {
        this(
            surface,
            coreEntry,
            coreEntry.summary,
            coreEntry.manual,
            EditorInfo.IME_ACTION_NEXT,
            dataField
        );
    }

    protected EmillaCommand(
        ActionSurface surface,
        AppEntry appEntry,
        int imeAction
    ) {
        this(
            surface,
            appEntry,
            appEntry.summary(),
            appEntry.actions.manual(),
            imeAction
        );
    }

    protected EmillaCommand(
        ActionSurface surface,
        AppEntry appEntry,
        DataField dataField
    ) {
        this(
            surface,
            appEntry,
            appEntry.summary(),
            appEntry.actions.manual(),
            EditorInfo.IME_ACTION_NEXT,
            dataField
        );
    }

    private EmillaCommand(
        ActionSurface surface,
        Params params,
        @StringRes int summary,
        @StringRes int manual,
        int imeAction,
        @Nullable DataField dataField
    ) {
        this.params = params;
        this.name = params.name(surface.getResources());
        this.summary = summary;
        this.manual = manual;
        mImeAction = imeAction;
        this.dataHint = dataField != null
            ? dataField.hint()
            : 0
        ;
    }

    @internal final void instruct(ActionSurface surface, @Nullable String instruction) {
        if (!Objects.equals(mInstruction, instruction)) {
            // we don't assume this is true because input editor bugs may cause
            // onTextChanged() to be called repeatedly for the same text.
            mInstruction = instruction;
            if (mActive) {
                onInstruct(surface, instruction);
            }
        }
    }

    public final void decorate(
        AssistActivity act,
        Resources res,
        boolean setIcon,
        boolean isDefault
    ) {
        CharSequence title = isDefault
            ? Lang.colonConcat(
                res,
                R.string.command_default,
                params.isProperNoun()
                    ? this.name
                    : this.name.toLowerCase()
                //
            ) : params.title(res)
        ;
        act.updateTitle(title);
        act.updateDataHint();
        act.setImeAction(mImeAction);
        if (setIcon) {
            act.setSubmitIcon(params.actionIcon(act));
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
    /// @param surface
    /// @param instruction is provided after in the command field after the command's name. It's
    /// always space-trimmed and should remain as such.
    protected abstract Feedback run(ActionSurface surface, String instruction);

    @Nullable
    protected @open Widget[] widgets() {
        return null;
    }

    @Nullable
    protected @open Gadget[] gadgets() {
        return null;
    }
}
