package net.emilla.content.retrieve;

import static net.emilla.chime.Chimer.RESUME;

import android.net.Uri;

import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts.PickMultipleVisualMedia;
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.content.receive.FileReceiver;

import java.util.List;

public class MediaRetriever extends ResultRetriever<PickVisualMediaRequest, List<Uri>, FileReceiver> {

    public MediaRetriever(AssistActivity act) {
        super(act, new PickMultipleVisualMedia());
    }

    public void retrieve(FileReceiver receiver) {
        if (alreadyHas(receiver)) return;
        launch(new PickVisualMediaRequest.Builder()
                .setMediaType(PickVisualMedia.ImageAndVideo.INSTANCE)
                .build());
    }

    @Override
    protected ResultCallback makeCallback() {
        return new MediaCallback();
    }

    private class MediaCallback extends ResultCallback {

        @Override
        protected void onActivityResult(List<Uri> media, FileReceiver receiver) {
            if (media.isEmpty()) activity.toast(R.string.toast_media_not_selected);
            else receiver.provide(media);
            activity.chime(RESUME);
        }
    }
}
