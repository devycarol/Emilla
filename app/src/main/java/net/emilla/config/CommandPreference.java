package net.emilla.config;

import static java.util.Objects.requireNonNull;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.SwitchCompat;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceViewHolder;

import net.emilla.R;
import net.emilla.app.AppEntry;

public final class CommandPreference extends EditTextPreference {

    public final String setKey;

    private final String mEnabledKey;

    public CommandPreference(Context ctx, @Nullable AttributeSet attrs) {
        super(ctx, attrs, R.attr.commandPreferenceStyle, 0);

        String entry = getKey();
        setKey(Aliases.textKey(entry));
        setKey = Aliases.setKey(entry);
        mEnabledKey = SettingVals.commandEnabledKey(entry);
    }

    /*internal*/ CommandPreference(Context ctx, AppEntry app) {
        super(ctx, null, R.attr.commandPreferenceStyle, 0);

        String entry = app.entry();
        setKey(Aliases.textKey(entry));
        setKey = Aliases.setKey(entry);
        mEnabledKey = SettingVals.appEnabledKey(app.pkg, app.cls);

        setTitle(app.label);
        @StringRes int summary = app.summary();
        if (summary != 0) setSummary(summary);
        setIcon(app.icon(ctx.getPackageManager()));
    }

    private boolean mChecked;

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
        return requireNonNull(getSharedPreferences());
    }

    @Override
    public boolean shouldDisableDependents() {
        return !mChecked || !isEnabled();
    }
}
