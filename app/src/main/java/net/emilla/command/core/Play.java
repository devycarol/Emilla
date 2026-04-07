package net.emilla.command.core;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.provider.MediaStore;
import android.view.inputmethod.EditorInfo;

import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.media.MediaControl;
import net.emilla.media.MediaType;
import net.emilla.util.Apps;
import net.emilla.util.Services;

final class Play extends CoreCommand {
    @internal Play(Context ctx) {
        super(ctx, CoreEntry.PLAY, EditorInfo.IME_ACTION_GO);
    }

    @Override
    protected void run(AssistActivity act) {
        AudioManager audio = Services.audio(act);
        MediaControl.play(audio);
        act.give(a -> {});
    }

    @Override
    protected void run(AssistActivity act, String media) {
        var intent = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
        // bruh please WHY does no one update the documentation to reflect that no one supports this
        // anymore
        intent.putExtra(SearchManager.QUERY, media);
        // note that at least an empty string is required in all cases

        var type = MediaType.of(media);
        intent.putExtra(MediaStore.EXTRA_MEDIA_FOCUS, type.focus);

        Apps.succeed(act, intent);
    }
}
