package net.emilla.action;

import android.content.res.Resources;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.command.core.Torch;

public class Flashlight implements QuickAction {

    private final Resources mRes;
    private final Torch mTorch;

    public Flashlight(AssistActivity act) {
        mRes = act.getResources();
        mTorch = new Torch(act, null);
    }

    @Override @IdRes
    public int id() {
        return R.id.action_flashlight;
    }

    @Override @DrawableRes
    public int icon() {
        return R.drawable.ic_torch;
    }

    @Override
    public String label() {
        return mRes.getString(R.string.action_flashlight);
    }

    @Override
    public String description() {
        return mRes.getString(R.string.action_desc_flashlight);
    }

    @Override
    public void perform() {
        mTorch.execute();
    }
}
