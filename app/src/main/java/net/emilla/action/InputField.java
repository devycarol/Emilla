package net.emilla.action;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import net.emilla.R;

public enum InputField {
    LOCATION(R.string.field_location, R.drawable.ic_location),
    URL(R.string.field_url, R.drawable.ic_web),
    // Todo: 'link' icon
    SUBJECT(R.string.field_subject, R.drawable.ic_subject),
;
    private static final InputField[] sValues = values();

    @StringRes
    public final int name;
    @DrawableRes
    public final int icon;

    InputField(@StringRes int name, @DrawableRes int icon) {
        this.name = name;
        this.icon = icon;
    }

    public static InputField of(int ordinal) {
        return sValues[ordinal];
    }
}
