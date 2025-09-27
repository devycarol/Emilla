package net.emilla.content.retrieve;

import android.net.Uri;

import androidx.activity.result.contract.ActivityResultContracts.GetMultipleContents;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.content.receive.FilesReceiver;

import java.util.List;

public final class FilesRetriever extends ResultRetriever<String, List<Uri>, FilesReceiver> {

    public FilesRetriever(AssistActivity act) {
        super(act, new GetMultipleContents());
    }

    public void retrieve(FilesReceiver receiver, String mimeType) {
        if (alreadyHas(receiver)) return;
        launch(mimeType);
    }

    @Override
    protected ResultCallback makeCallback() {
        return new FileCallback();
    }

    private /*inner*/ final class FileCallback extends ResultCallback {

        @Override
        protected void onActivityResult(List<Uri> files, FilesReceiver receiver) {
            if (files.isEmpty()) pActivity.toast(R.string.toast_files_not_selected);
            else receiver.provide(files);
        }
    }
}
