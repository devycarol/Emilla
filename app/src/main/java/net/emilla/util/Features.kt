@file:JvmName("Features")

package net.emilla.util

import android.content.pm.PackageManager
import android.os.Build

@JvmName("camera")
fun PackageManager.hasCameraFeature(): Boolean {
    return hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
}

@JvmName("phone")
fun PackageManager.hasPhoneFeature(): Boolean {
    return hasSystemFeature(PackageManager.FEATURE_TELEPHONY)
}

@JvmName("sms")
fun PackageManager.hasSmsFeature(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        hasSystemFeature(PackageManager.FEATURE_TELEPHONY_MESSAGING)
    } else {
        hasSystemFeature(PackageManager.FEATURE_TELEPHONY)
    }
}

@JvmName("torch")
fun hasTorchFeature(pm: PackageManager): Boolean {
    // todo: it'd be a good idea to test this on a minSdk device.
    return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
}
