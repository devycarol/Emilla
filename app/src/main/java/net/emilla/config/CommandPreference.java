package net.emilla.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceViewHolder;

import net.emilla.R;
import net.emilla.command.app.AppEntry;
import net.emilla.command.core.CoreEntry;

import java.util.Objects;

public final class CommandPreference extends EditTextPreference {

    public final String setKey;

    private final String mEnabledKey;

    public CommandPreference(Context ctx, @Nullable AttributeSet attrs) {
        this(ctx, attrs, null);
    }

    /*internal*/ CommandPreference(Context ctx, CoreEntry coreEntry) {
        this(ctx, null, coreEntry.entry);

        setTitle(coreEntry.name);
        setSummary(coreEntry.summary);
        setIcon(coreEntry.icon(ctx));
    }

    /*internal*/ CommandPreference(Context ctx, AppEntry appEntry) {
        this(ctx, null, appEntry.entry());

        setTitle(appEntry.label);
        setSummary(appEntry.summary());
        setIcon(appEntry.icon(ctx));
    }

    private CommandPreference(Context ctx, @Nullable AttributeSet attrs, @Nullable String entry) {
        super(ctx, attrs, R.attr.commandPreferenceStyle, 0);

        if (entry == null) {
            entry = getKey();
        }

        setKey(Aliases.textKey(entry));
        setKey = Aliases.setKey(entry);

        mEnabledKey = SettingVals.commandEnabledKey(entry);
    }

    private boolean mChecked = false;

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        boolean enabled = isEnabled();
        mChecked = enabled && commandEnabled(true);

        var switchView = (SwitchCompat) holder.findViewById(R.id.switch_widget);
        switchView.setOnCheckedChangeListener(null);
        switchView.setChecked(mChecked);
        if (enabled) switchView.setClickable(true);
        switchView.setOnCheckedChangeListener(this::setChecked);
    }

    private boolean mCheckedSet = false;

    private void setChecked(CompoundButton switchView, boolean checked) {
        boolean changed = mChecked != checked;
        if (changed || !mCheckedSet) {
            mChecked = checked;
            mCheckedSet = true;

            if (checked != commandEnabled(!checked)) {
                prefs().edit().putBoolean(mEnabledKey, checked).apply();
            }

            if (changed) {
                notifyDependencyChange(shouldDisableDependents());
                notifyChanged();
            }
        }
    }

    private boolean commandEnabled(boolean defaultValue) {
        return prefs().getBoolean(mEnabledKey, defaultValue);
    }

    private SharedPreferences prefs() {
        return Objects.requireNonNull(getSharedPreferences());
    }

    @Override
    public boolean shouldDisableDependents() {
        return !mChecked || !isEnabled();
    }

}
