package net.emilla.config;

import static java.util.Objects.requireNonNull;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceViewHolder;

import net.emilla.R;

public final class CommandPreference extends EditTextPreference {

    private final String mEnabledKey;

    public CommandPreference(Context ctx, @Nullable AttributeSet attrs) {
        super(ctx, attrs, R.attr.commandPreferenceStyle, 0);

        String key = getKey();
        setKey(Aliases.textKey(key));
        mEnabledKey = SettingVals.commandEnabledKey(key);
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
