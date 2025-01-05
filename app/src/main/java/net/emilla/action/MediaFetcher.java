package net.emilla.action;

import android.content.res.Resources;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.content.receive.FileReceiver;

public class MediaFetcher implements QuickAction {

    public static final int ID = R.id.action_get_media;

    private final AssistActivity mActivity;
    private final Resources mRes;
    private final FileReceiver mReceiver;

    public MediaFetcher(AssistActivity act, FileReceiver receiver) {
        mActivity = act;
        mRes = act.getResources();
        mReceiver = receiver;
    }

    @Override @IdRes
    public int id() {
        return ID;
    }

    @Override @DrawableRes
    public int icon() {
        return R.drawable.ic_media;
    }

    @Override
    public String label() {
        return mRes.getString(R.string.action_attach_media);
    }

    @Override
    public String description() {
        return mRes.getString(R.string.action_desc_attach_media);
    }

    @Override
    public void perform() {
        mActivity.offerMedia(mReceiver);
    }
}
