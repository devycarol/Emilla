package net.emilla.util;

import android.content.pm.PackageManager;
import android.os.Build;

public enum Features {
    ;

    public static boolean camera(PackageManager pm) {
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    public static boolean phone(PackageManager pm) {
        return pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
    }

    public static boolean sms(PackageManager pm) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            ? pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_MESSAGING)
            : pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
    }

    public static boolean torch(PackageManager pm) {
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        // todo: test this on a minSdk device.
    }

}
