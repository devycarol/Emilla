package net.emilla.chime;

import android.content.Context;
import android.media.MediaPlayer;

import androidx.annotation.RawRes;

import net.emilla.R;

public class Nebula implements Chimer {
    @RawRes
    static int sound(byte chime) {
        return switch (chime) {
            case Chimer.START -> R.raw.nebula_start;
            case Chimer.ACT -> R.raw.nebula_act;
            case Chimer.PEND -> R.raw.nebula_pend;
            case Chimer.RESUME -> R.raw.nebula_resume;
            case Chimer.EXIT -> R.raw.nebula_exit;
            case Chimer.SUCCEED -> R.raw.nebula_succeed;
            case Chimer.FAIL -> R.raw.nebula_fail;
            default -> -1;
        };
    }

    private final Context mContext;

    public Nebula(Context ctx) {
        mContext = ctx;
    }

    @Override
    public void chime(byte id) {
        // Todo: still encountering occasional sound cracking issues
        MediaPlayer player = MediaPlayer.create(mContext, sound(id));
        player.setOnCompletionListener(MediaPlayer::release);
        player.start();
    }
}
