package net.emilla.action.field;

import androidx.annotation.IdRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;

public final class LocationField extends FieldToggle {

    @IdRes
    public static final int ACTION_ID = R.id.action_field_location;
    @IdRes
    public static final int FIELD_ID = R.id.field_location;

    public LocationField(AssistActivity act) {
        super(act, ACTION_ID, FIELD_ID, R.string.field_location, R.drawable.ic_location);
    }
}
