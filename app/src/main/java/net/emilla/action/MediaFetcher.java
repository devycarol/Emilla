package net.emilla.action;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.content.receive.FilesReceiver;

public final class MediaFetcher implements LabeledQuickAction {

    private final AssistActivity mActivity;
    private final FilesReceiver mReceiver;

    public MediaFetcher(AssistActivity act, String commandEntry) {
        mActivity = act;
        mReceiver = new FilesReceiver(act, commandEntry);
    }

    @Override @IdRes
    public int id() {
        return R.id.action_get_media;
    }

    @Override @DrawableRes
    public int icon() {
        return R.drawable.ic_media;
    }

    @Override @StringRes
    public int label() {
        return R.string.action_attach_media;
    }

    @Override @StringRes
    public int description() {
        return R.string.action_desc_attach_media;
    }

    @Override
    public void perform() {
        mActivity.offerMedia(mReceiver);
    }
}
