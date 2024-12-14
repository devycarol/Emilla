package net.emilla.command.core;

import static net.emilla.chime.Chimer.ACT;
import static net.emilla.chime.Chimer.RESUME;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.DrawableRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exceptions.EmlaBadCommandException;
import net.emilla.system.AppEmilla;

public class Torch extends CoreCommand {
    private static String cameraId(CameraManager camMgr) throws CameraAccessException {
        String[] ids = camMgr.getCameraIdList();
        for (String id : ids) {
            CameraCharacteristics c = camMgr.getCameraCharacteristics(id);
            Boolean flashAvailable = c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            Integer lensFacing = c.get(CameraCharacteristics.LENS_FACING);
            if (flashAvailable != null && flashAvailable
                    && lensFacing != null && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                return id;
            }
        }
        return null;
        // TODO: what if multiple torches?
    }

    @Override @DrawableRes
    public int icon() {
        return R.drawable.ic_torch;
    }

    @Override
    public int imeAction() {
        return EditorInfo.IME_ACTION_DONE;
    }

    public Torch(AssistActivity act, String instruct) {
        super(act, instruct, R.string.command_torch, R.string.instruction_torch);
    }

    @Override
    protected void run() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) throw new EmlaBadCommandException("Sorry! This command doesn't support your Android version yet.");
        // TODO: https://github.com/LineageOS/android_packages_apps_Torch
        CameraManager camMgr = (CameraManager) activity().getSystemService(Context.CAMERA_SERVICE);
        try {
            String camId = cameraId(camMgr);
            if (camId == null) return;
            if (AppEmilla.sTorching) {
                camMgr.setTorchMode(camId, false);
                chime(RESUME);
                // TODO ACC: if you can't see the torch, this feedback is critically insufficient.
                AppEmilla.sTorching = false;
            } else {
                camMgr.setTorchMode(camId, true);
                chime(ACT);
                // TODO ACC: if you can't see the torch, this feedback is critically insufficient.
                AppEmilla.sTorching = true;
            }
        } catch (CameraAccessException ignored) {} // Torch not toggled, nothing to do.
    }

    @Override
    protected void run(String ignored) {
        run(); // TODO: instead, this should revert to the default command
    }
}
