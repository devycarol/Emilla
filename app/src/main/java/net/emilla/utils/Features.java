package net.emilla.utils;

import android.content.pm.PackageManager;

public class Features {
public static boolean torch(PackageManager pm) {
    // It'd be a good idea to test this on a minSdk device.
    return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
}

private Features() {}
}
