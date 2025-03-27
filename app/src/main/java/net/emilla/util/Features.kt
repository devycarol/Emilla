package net.emilla.util;

import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

public final class Features {

    public static boolean phone(PackageManager pm) {
        return pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static boolean sms(PackageManager pm) {
        return pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_MESSAGING);
    }

    public static boolean torch(PackageManager pm) {
        // todo: it'd be a good idea to test this on a minSdk device.
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    private Features() {}
}
