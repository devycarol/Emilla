package net.emilla.util;

import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.ACTION_UP;

import android.media.AudioManager;
import android.view.KeyEvent;

public enum MediaControl {
    ;

    public static void play(AudioManager am) {
        sendButtonEvent(am, KeyEvent.KEYCODE_MEDIA_PLAY);
    }

    public static void pause(AudioManager am) {
        sendButtonEvent(am, KeyEvent.KEYCODE_MEDIA_PAUSE);
    }

    public static void playPause(AudioManager am) {
        sendButtonEvent(am, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
    }

    private static void sendButtonEvent(AudioManager am, int keyCode) {
        am.dispatchMediaKeyEvent(new KeyEvent(ACTION_DOWN, keyCode));
        am.dispatchMediaKeyEvent(new KeyEvent(ACTION_UP, keyCode));
    }

}
