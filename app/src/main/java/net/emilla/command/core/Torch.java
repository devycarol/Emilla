package net.emilla.command.core;

import static net.emilla.chime.Chimer.ACT;
import static net.emilla.chime.Chimer.RESUME;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.settings.Aliases;

public final class Torch extends CoreCommand {

    public static final String ENTRY = "torch";
    @StringRes
    public static final int NAME = R.string.command_torch;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_torch;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static Yielder yielder() {
        return new Yielder(false, Torch::new, ENTRY, NAME, ALIASES);
    }

    private static final class TorchParams extends CoreParams {

        private TorchParams() {
            super(NAME,
                  R.string.instruction_torch,
                  R.drawable.ic_torch,
                  EditorInfo.IME_ACTION_DONE,
                  R.string.summary_torch,
                  R.string.manual_torch);
        }
    }

    private static String flashCameraId(CameraManager camMgr) throws CameraAccessException {
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

    private static boolean sTorching = false;
    // TODO: replace this with a query - this isn't fully reliable for detecting toggle state. e.g.
    //  what if the torch was turned on before the app was started?

    public Torch(AssistActivity act) {
        super(act, new TorchParams());
    }

    @Override
    protected void run() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) throw badCommand(R.string.error_unfinished_version);
        // TODO: https://github.com/LineageOS/android_packages_apps_Torch
        var camMgr = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
    try {
        String camId = flashCameraId(camMgr);
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
    protected void run(@NonNull String ignored) {
        run(); // Todo: remove this from the interface for non-instructables.
    }
}
