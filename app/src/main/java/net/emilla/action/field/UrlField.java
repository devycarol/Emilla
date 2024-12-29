package net.emilla.action.field;

import androidx.annotation.IdRes;

import net.emilla.AssistActivity;
import net.emilla.R;

public class UrlField extends FieldToggle {

    @IdRes
    public static final int ACTION_ID = R.id.action_field_url;
    @IdRes
    public static final int FIELD_ID = R.id.field_url;

    public UrlField(AssistActivity act) {
        super(act, ACTION_ID, FIELD_ID, R.string.field_url, R.drawable.ic_web);
    }
}
