package net.emilla.media;

import android.app.SearchManager;
import android.content.Intent;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Albums;
import android.provider.MediaStore.Audio.Artists;
import android.provider.MediaStore.Audio.Genres;
import android.provider.MediaStore.Audio.Media;
import android.provider.MediaStore.Audio.Playlists;

import net.emilla.annotation.open;

public enum MediaType {
    ANY("vnd.android.cursor.item/*", SearchManager.QUERY) {
    },
    GENRE(Genres.ENTRY_CONTENT_TYPE, MediaStore.EXTRA_MEDIA_GENRE) {
    },
    ARTIST(Artists.ENTRY_CONTENT_TYPE, MediaStore.EXTRA_MEDIA_ARTIST) {
    },
    ALBUM(Albums.ENTRY_CONTENT_TYPE, MediaStore.EXTRA_MEDIA_ALBUM) {
    },
    SONG(Media.ENTRY_CONTENT_TYPE, MediaStore.EXTRA_MEDIA_TITLE) {
    },
    PLAYLIST(Playlists.ENTRY_CONTENT_TYPE, MediaStore.EXTRA_MEDIA_PLAYLIST) {
    },
//    AUDIOBOOK(todo, todo) {
//        @Override
//        public boolean putExtras(Intent intent, String... extras) {
//            return todo;
//        }
//    },
//    PODCAST(todo, todo) {
//        @Override
//        public boolean putExtras(Intent intent, String... extras) {
//            return todo;
//        }
//    },
;
    private static final MediaType[] sValues = values();

    public final String focus;
    private final String mExtra;

    MediaType(String focus, String extra) {
        this.focus = focus;
        mExtra = extra;
    }

    public static MediaType of(String media) {
        // Todo
        return ANY;
    }

    public @open boolean putExtras(Intent intent, String... extras) {
        int i = extras.length - 1;
        if (i != ordinal()) {
            return false;
        }

        intent.putExtra(mExtra, extras[i]);

        while (i > 0) {
            --i;
            intent.putExtra(sValues[i].mExtra, extras[i]);
        }

        return true;
    }
}
