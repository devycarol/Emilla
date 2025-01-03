package net.emilla.content;

import android.net.Uri;
import android.util.Log;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.GetMultipleContents;

import net.emilla.EmillaActivity;
import net.emilla.R;

import java.util.List;

public class FileContract {

    private static final String TAG = FileContract.class.getSimpleName();

    private final EmillaActivity mActivity;
    private final ActivityResultLauncher<String> mLauncher;
    private AttachReceiver mReceiver;

    public FileContract(EmillaActivity act) {
        mActivity = act;
        mLauncher = act.registerForActivityResult(new GetMultipleContents(), new FileCallback());
    }

    public void retrieve(AttachReceiver receiver, String mimeType) {
        if (mReceiver != null) {
            Log.d(TAG, "retrieve: result launcher already engaged. Not launching again.");
            return;
        }

        mReceiver = receiver;
        mLauncher.launch(mimeType);
    }

    private class FileCallback implements ActivityResultCallback<List<Uri>> {

        @Override
        public void onActivityResult(List<Uri> files) {
            if (files.isEmpty()) mActivity.toast(R.string.toast_files_not_selected);
            else mReceiver.provide(files);
            mReceiver = null;
        }
    }
}
