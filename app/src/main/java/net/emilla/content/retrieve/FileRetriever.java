package net.emilla.content.retrieve;

import android.net.Uri;

import androidx.activity.result.contract.ActivityResultContracts.GetMultipleContents;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.content.receive.FileReceiver;

import java.util.List;

public class FileRetriever extends ResultRetriever<String, List<Uri>, FileReceiver> {

    public FileRetriever(AssistActivity act) {
        super(act, new GetMultipleContents());
    }

    public void retrieve(FileReceiver receiver, String mimeType) {
        if (alreadyHas(receiver)) return;
        launch(mimeType);
    }

    @Override
    protected ResultCallback makeCallback() {
        return new FileCallback();
    }

    private class FileCallback extends ResultCallback {

        @Override
        protected void onActivityResult(List<Uri> files, FileReceiver receiver) {
            if (files.isEmpty()) activity.toast(R.string.toast_files_not_selected);
            else receiver.provide(files);
        }
    }
}
