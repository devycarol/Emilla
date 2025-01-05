package net.emilla.action;

import android.content.res.Resources;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.content.receive.FileReceiver;

public class FileFetcher implements QuickAction {

    public static final int ID = R.id.action_get_files;

    private final AssistActivity mActivity;
    private final Resources mRes;
    private final FileReceiver mReceiver;
    private final String mMimeType;

    public FileFetcher(AssistActivity act, FileReceiver receiver, String mimeType) {
        mActivity = act;
        mRes = act.getResources();
        mReceiver = receiver;
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

    @Override
    public String label() {
        return mRes.getString(R.string.action_attach_files);
    }

    @Override
    public String description() {
        return mRes.getString(R.string.action_desc_attach_files);
    }

    @Override
    public void perform() {
        mActivity.offerFiles(mReceiver, mMimeType);
        // TODO: visual feedback via attachment manager widget
    }
}
