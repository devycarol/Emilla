package net.emilla.util;

import static net.emilla.chime.Chime.ACT;
import static net.emilla.chime.Chime.RESUME;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;

import androidx.annotation.Nullable;

import net.emilla.activity.AssistActivity;

public enum TorchManager {;
    private static boolean sTorching = false;
    // TODO: replace this with a query, this isn't fully reliable for detecting toggle state. e.g.
    //  when torch is toggled outside this app.

    public static boolean toggle(AssistActivity act) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false;
            // TODO: https://github.com/LineageOS/android_packages_apps_Torch
        }

        CameraManager camera = Services.camera(act);
        try {
            String cameraId = flashCameraId(camera);
            if (cameraId == null) {
                return false;
            }

            if (sTorching) {
                camera.setTorchMode(cameraId, false);
                act.chime(RESUME);
                sTorching = false;
            } else {
                camera.setTorchMode(cameraId, true);
                act.chime(ACT);
                sTorching = true;
            }

            return true;
        } catch (CameraAccessException e) {
            return false;
        }
    }

    @Nullable
    private static String flashCameraId(CameraManager camera) throws CameraAccessException {
        for (String id : camera.getCameraIdList()) {
            var c12cs = camera.getCameraCharacteristics(id);
            if (Boolean.TRUE.equals(c12cs.get(CameraCharacteristics.FLASH_INFO_AVAILABLE))) {
                Integer lensFacing = c12cs.get(CameraCharacteristics.LENS_FACING);
                if (lensFacing != null && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                    return id;
                }
            }
        }
        return null;
        // Todo: what if multiple torches?
    }
}
