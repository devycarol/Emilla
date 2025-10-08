package net.emilla.system;

import static android.content.Intent.ACTION_VOICE_COMMAND;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.accessibilityservice.AccessibilityButtonController;
import android.accessibilityservice.AccessibilityButtonController.AccessibilityButtonCallback;
import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.RequiresApi;

public final class EmillaA11yService extends AccessibilityService {

    // TODO: google assistant (maybe?) changes the accessibility menu icon for "assistant," so we
    //  should also do this. I wonder if you can add items to that menu..

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {}

    @Override
    public void onInterrupt() {}

    @Override @RequiresApi(api = Build.VERSION_CODES.O)
    public void onCreate() {
        var controller = getAccessibilityButtonController();
        var callback = new AssistButtonCallback();
        controller.registerAccessibilityButtonCallback(callback);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private /*inner*/ final class AssistButtonCallback extends AccessibilityButtonCallback {

        @Override
        public void onClicked(AccessibilityButtonController controller) {
            startActivity(new Intent(ACTION_VOICE_COMMAND).addFlags(FLAG_ACTIVITY_NEW_TASK));
        }
    }
}
