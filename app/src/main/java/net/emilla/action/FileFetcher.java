package net.emilla.action;

import android.content.res.Resources;
import android.net.Uri;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.content.AttachReceiver;

import java.util.List;

public class FileFetcher implements QuickAction, AttachReceiver {

    public static final int ID = R.id.action_get_files;

    private final AssistActivity mActivity;
    private final Resources mRes;
    private final AttachReceiver mReceiver;
    private final String mMimeType;

    public FileFetcher(AssistActivity act, AttachReceiver receiver, String mimeType) {
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
        mActivity.retrieveFiles(this, mMimeType);
    }

    @Override
    public void provide(List<Uri> attachments) {
        // TODO: visual feedback via attachment manager widget
        mReceiver.provide(attachments);
    }
}
