package net.emilla.action.field;

import static net.emilla.chime.Chimer.ACT;
import static net.emilla.chime.Chimer.RESUME;

import android.content.res.Resources;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.action.QuickAction;

public abstract class FieldToggle implements QuickAction {

    @IdRes
    private final int mActionId;
    @IdRes
    private final int mFieldId;
    @StringRes
    private final int mFieldName;
    @DrawableRes
    private final int mIcon;

    private final AssistActivity mActivity;

    private boolean mActivated = false;
    private EditText mField;

    public FieldToggle(AssistActivity act, @IdRes int action, @IdRes int field,
            @StringRes int fieldName, @DrawableRes int icon) {
        mActionId = action;
        mFieldId = field;
        mFieldName = fieldName;
        mIcon = icon;

        mActivity = act;
    }

    public boolean activated() {
        return mActivated;
    }

    public String fieldText() {
        return mField == null || mField.getVisibility() == View.GONE || mField.length() == 0 ? null
                : mField.getText().toString();
    }

    @Override @IdRes
    public int id() {
        return mActionId;
    }

    @Override @DrawableRes
    public int icon() {
        return mIcon;
    }

    @Override
    public String label(Resources res) {
        return res.getString(R.string.action_toggle_field, res.getString(mFieldName));
    }

    @Override
    public String description(Resources res) {
        return res.getString(R.string.action_desc_toggle_field, res.getString(mFieldName));
    }

    @Override
    public void perform() {
        if (mField == null) {
            mField = mActivity.createField(mFieldId, mFieldName);
            mActivated = true;
            mActivity.chime(ACT);
        } else {
            mActivated = mActivity.toggleField(mFieldId);
            mActivity.chime(mActivated ? ACT : RESUME);
        }
    }
}
