package net.emilla.command.core;

import static net.emilla.chime.Chimer.ACT;
import static net.emilla.chime.Chimer.RESUME;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.view.inputmethod.EditorInfo;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exception.EmlaBadCommandException;

public class Torch extends CoreCommand {

    public static final String ENTRY = "torch";

    private static class TorchParams extends CoreParams {

        private TorchParams() {
            super(R.string.command_torch,
                  R.string.instruction_torch,
                  R.drawable.ic_torch,
                  EditorInfo.IME_ACTION_DONE);
        }
    }

    private static boolean sTorching = false;
    // TODO: replace this with a query - this isn't fully reliable for detecting toggle state. e.g.
    //  what if the torch was turned on before the app was started?

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
        // Todo: what if multiple torches?
    }

    public Torch(AssistActivity act, String instruct) {
        super(act, instruct, new TorchParams());
    }

    @Override
    protected void run() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) throw new EmlaBadCommandException(R.string.command_torch, R.string.error_unfinished_version);
        // TODO: https://github.com/LineageOS/android_packages_apps_Torch
        CameraManager camMgr = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            String camId = cameraId(camMgr);
            if (camId == null) return;
            if (sTorching) {
                camMgr.setTorchMode(camId, false);
                chime(RESUME);
                // TODO ACC: if you can't see the torch, this feedback is critically insufficient.
                sTorching = false;
            } else {
                camMgr.setTorchMode(camId, true);
                chime(ACT);
                // TODO ACC: if you can't see the torch, this feedback is critically insufficient.
                sTorching = true;
            }
        } catch (CameraAccessException ignored) {} // Torch not toggled, nothing to do.
    }

    @Override
    protected void run(String ignored) {
        run(); // TODO: instead, this should revert to the default command
    }
}
