package net.emilla.content.receive;

import android.net.Uri;

import net.emilla.R;
import net.emilla.activity.AssistActivity;

import java.util.ArrayList;
import java.util.List;

public final class FilesReceiver implements ResultReceiver {

    private final AssistActivity mActivity;
    private final String mCommandEntry;

    public FilesReceiver(AssistActivity act, String commandEntry) {
        mActivity = act;
        mCommandEntry = commandEntry;
    }

    public void provide(List<Uri> attachments) {
        if (attachments.isEmpty()) return;

        ArrayList<Uri> attaches = mActivity.attachments(mCommandEntry);
        if (attaches == null) attaches = new ArrayList<>(attachments);
        else for (Uri attachment : attachments) {
            int index = attaches.indexOf(attachment);
            if (index == -1) attaches.add(attachment);
            else attaches.remove(index); // TODO: better attachment UI.
        }

        int size = attaches.size();
        if (size == 0) attaches = null;

        mActivity.putAttachments(mCommandEntry, attaches);

        var res = mActivity.getResources();
        mActivity.toast(res.getQuantityString(R.plurals.toast_files_attached, size, size));
        // Todo: better feedback.
    }
}
