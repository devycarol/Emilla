package net.emilla.system;

import static android.content.Intent.ACTION_VOICE_COMMAND;

import android.accessibilityservice.AccessibilityButtonController;
import android.accessibilityservice.AccessibilityButtonController.AccessibilityButtonCallback;
import android.accessibilityservice.AccessibilityService;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.RequiresApi;

import net.emilla.utils.Apps;

public class EmillaAccessibilityService extends AccessibilityService {
private static final String TAG = "AccessibilityService";
// TODO: google assistant changes the accessibility menu icon for "assistant," so we should also do this.
//  I wonder if you can add items to that menu..
@Override
public void onAccessibilityEvent(final AccessibilityEvent event) {}

@Override
public void onInterrupt() {}

@RequiresApi(api = Build.VERSION_CODES.O)
@Override
public void onCreate() {
    final AccessibilityButtonController controller = getAccessibilityButtonController();
    controller.registerAccessibilityButtonCallback(new AccessibilityButtonCallback() {
        @Override
        public void onClicked(final AccessibilityButtonController controller) {
            startActivity(Apps.newTask(ACTION_VOICE_COMMAND));
            // No need to validate this intent since we provide it ourself ;)
        }

//        @Override
//        public void onAvailabilityChanged(final AccessibilityButtonController controller,
//                final boolean available) {}
    });
}
}
