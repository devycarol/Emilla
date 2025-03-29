package net.emilla.action;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.content.receive.FilesReceiver;

public final class FileFetcher implements LabeledQuickAction {

    public static final int ID = R.id.action_get_files;

    private final AssistActivity mActivity;
    private final FilesReceiver mReceiver;
    private final String mMimeType;

    public FileFetcher(AssistActivity act, String commandEntry, String mimeType) {
        mActivity = act;
        mReceiver = new FilesReceiver(act, commandEntry);
        mMimeType = mimeType;
    }

    @Override @IdRes
    public int id() {
        return ID;
    }

    @Override @DrawableRes
    public int icon() {
        return R.drawable.ic_attach;
    }

    @Override @StringRes
    public int label() {
        return R.string.action_attach_files;
    }

    @Override @StringRes
    public int description() {
        return R.string.action_desc_attach_files;
    }

    @Override
    public void perform() {
        mActivity.offerFiles(mReceiver, mMimeType);
        // TODO: visual feedback via attachment manager widget
    }
}
