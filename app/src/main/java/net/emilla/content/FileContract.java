package net.emilla.content;

import android.net.Uri;

import androidx.activity.result.contract.ActivityResultContracts.GetMultipleContents;

import net.emilla.AssistActivity;
import net.emilla.R;

import java.util.List;

public class FileContract extends ResultContract<String, List<Uri>, FileReceiver> {

    public FileContract(AssistActivity act) {
        super(act, new GetMultipleContents());
    }

    public void retrieve(FileReceiver receiver, String mimeType) {
        if (alreadyHas(receiver)) return;
        launcher.launch(mimeType);
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
