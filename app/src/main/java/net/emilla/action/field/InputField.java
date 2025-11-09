package net.emilla.action.field;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;

public enum InputField {
    LOCATION(
        R.id.action_field_location,
        R.id.field_location,
        R.string.field_location,
        R.drawable.ic_location
    ),
    URL(
        R.id.action_field_url,
        R.id.field_url,
        R.string.field_url,
        R.drawable.ic_web
    ),
    SUBJECT(
        R.id.action_field_subject,
        R.id.field_subject,
        R.string.field_subject,
        R.drawable.ic_subject
    );

    @IdRes
    public final int actionId;
    @IdRes
    public final int fieldId;
    @StringRes
    public final int fieldName;
    @DrawableRes
    public final int icon;

    InputField(
        @IdRes int actionId,
        @IdRes int fieldId,
        @StringRes int fieldName,
        @DrawableRes int icon
    ) {
        this.actionId = actionId;
        this.fieldId = fieldId;
        this.fieldName = fieldName;
        this.icon = icon;
    }

    public FieldToggle toggler(AssistActivity act) {
        return new FieldToggle(act, this);
    }

}
