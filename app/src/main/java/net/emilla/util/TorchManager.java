package net.emilla.util;

import static net.emilla.chime.Chimer.ACT;
import static net.emilla.chime.Chimer.RESUME;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.exception.EmillaException;

public final class TorchManager {

    private static final String TAG = TorchManager.class.getSimpleName();

    private static boolean sTorching = false;
    // TODO: replace this with a query, this isn't fully reliable for detecting toggle state. e.g.
    //  when torch is toggled outside this app.

    public static void toggle(AssistActivity act, @StringRes int errorTitle) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            throw new EmillaException(errorTitle, R.string.error_unfinished_version);
            // TODO: https://github.com/LineageOS/android_packages_apps_Torch
        }
        CameraManager camMgr = Services.camera(act);
    try {
        String camId = flashCamId(camMgr);
        if (camId == null) {
            throw new EmillaException(errorTitle, R.string.error_torch_failed);
            // Todo: prevent the command if no torch feature. quick-action is already blocked.
        }

        if (sTorching) {
            camMgr.setTorchMode(camId, false);
            act.chime(RESUME);
            sTorching = false;
        } else {
            camMgr.setTorchMode(camId, true);
            act.chime(ACT);
            sTorching = true;
        }
    } catch (CameraAccessException e) {
        Log.e(TAG, "Failed to toggle the flashlight.", e);
        throw new EmillaException(errorTitle, R.string.error_torch_failed);
    }}

    @Nullable
    private static String flashCamId(CameraManager camMgr) throws CameraAccessException {
        String[] ids = camMgr.getCameraIdList();
        for (String id : ids) {
            CameraCharacteristics c = camMgr.getCameraCharacteristics(id);
            Boolean flashAvailable = c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            if (Boolean.TRUE.equals(flashAvailable)) {
                Integer lensFacing = c.get(CameraCharacteristics.LENS_FACING);
                if (lensFacing != null && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                    return id;
                }
            }
        }
        return null;
        // Todo: what if multiple torches?
    }

    private TorchManager() {}
}
