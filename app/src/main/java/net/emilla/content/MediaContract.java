package net.emilla.content;

import static net.emilla.chime.Chimer.RESUME;

import android.net.Uri;
import android.util.Log;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts.PickMultipleVisualMedia;
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia;

import net.emilla.AssistActivity;
import net.emilla.R;

import java.util.List;

public class MediaContract {

    private static final String TAG = MediaContract.class.getSimpleName();

    private final AssistActivity mActivity;
    private final ActivityResultLauncher<PickVisualMediaRequest> mLauncher;
    private FileReceiver mReceiver;

    public MediaContract(AssistActivity act) {
        mActivity = act;
        mLauncher = act.registerForActivityResult(new PickMultipleVisualMedia(), new MediaCallback());
    }

    public void retrieve(FileReceiver receiver) {
        if (mReceiver != null) {
            Log.d(TAG, "retrieve: result launcher already engaged. Not launching again.");
            return;
        }

        mReceiver = receiver;
        mLauncher.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(PickVisualMedia.ImageAndVideo.INSTANCE)
                .build());
    }

    private class MediaCallback implements ActivityResultCallback<List<Uri>> {

        @Override
        public void onActivityResult(List<Uri> media) {
            if (media.isEmpty()) mActivity.toast(R.string.toast_media_not_selected);
            else mReceiver.provide(media);
            mReceiver = null;
            mActivity.chime(RESUME);
        }
    }
}
