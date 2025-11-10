package net.emilla.action.field;

import static net.emilla.chime.Chime.ACT;
import static net.emilla.chime.Chime.RESUME;

import android.content.res.Resources;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;

import net.emilla.R;
import net.emilla.action.QuickAction;
import net.emilla.activity.AssistActivity;

public final class FieldToggle implements QuickAction {

    private final AssistActivity mActivity;
    private final InputField mInputField;

    private boolean mActivated = false;
    private EditText mField = null;

    /*internal*/ FieldToggle(AssistActivity act, InputField inputField) {
        mActivity = act;
        mInputField = inputField;
    }

    @Nullable
    public String fieldText() {
        if (mField != null && mField.getVisibility() != View.GONE && mField.length() > 0) {
            return mField.getText().toString();
        }
        return null;
    }

    @Override @IdRes
    public int id() {
        return mInputField.actionId;
    }

    @Override @DrawableRes
    public int icon() {
        return mInputField.icon;
    }

    @Override
    public String label(Resources res) {
        return res.getString(R.string.action_toggle_field, res.getString(mInputField.fieldName));
    }

    @Override
    public String description(Resources res) {
        return res.getString(R.string.action_desc_toggle_field, res.getString(mInputField.fieldName));
    }

    @Override
    public void perform() {
        if (mField == null) {
            mField = mActivity.createField(mInputField.fieldId, mInputField.fieldName);
            mActivated = true;
            mActivity.chime(ACT);
        } else {
            mActivated = mActivity.toggleField(mInputField.fieldId);
            mActivity.chime(mActivated ? ACT : RESUME);
        }
    }

    @Override
    public void init(AssistActivity act) {
        QuickAction.super.init(act);

        if (mActivated) {
            act.reshowField(mInputField.fieldId);
        }
    }

    @Override
    public void cleanup(AssistActivity act) {
        QuickAction.super.cleanup(act);

        act.hideField(mInputField.fieldId);
    }

}
