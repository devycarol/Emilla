package net.emilla.action.field;

import androidx.annotation.IdRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;

public final class SubjectField extends FieldToggle {

    @IdRes
    public static final int ACTION_ID = R.id.action_field_subject;
    @IdRes
    public static final int FIELD_ID = R.id.field_subject;

    public SubjectField(AssistActivity act) {
        super(act, ACTION_ID, FIELD_ID, R.string.field_subject, R.drawable.ic_subject);
    }
}
