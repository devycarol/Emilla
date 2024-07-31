package net.emilla.system;

import android.accessibilityservice.AccessibilityButtonController;
import android.accessibilityservice.AccessibilityButtonController.AccessibilityButtonCallback;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.os.Build;
import android.os.Handler;
import android.view.accessibility.AccessibilityEvent;

import net.emilla.AssistActivity;
import net.emilla.utils.Apps;

public class EmillaAccessibilityService extends AccessibilityService {
// TODO: google assistant changes the accessibility menu icon for "assistant," so we should also do this.
//  I wonder if you can add items to that menu..
@Override
public void onAccessibilityEvent(final AccessibilityEvent event) {}

@Override
public void onInterrupt() {}

@Override
protected void onServiceConnected() {
// TODO: this isn't always getting set up properly. need to bind the service ASAP when wanted
//  rel: setting to start the service on boot. if the accessibility service is enabled, that'd
//  probably need to be forced-on.
// the linter is broken.. freely simplify to "if-less-return" if it starts working properly.
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    final AccessibilityServiceInfo info = getServiceInfo();
    info.flags |= AccessibilityServiceInfo.FLAG_REQUEST_ACCESSIBILITY_BUTTON;
    setServiceInfo(info);

    final AccessibilityButtonController controller = getAccessibilityButtonController();
    final AccessibilityButtonCallback mCallback = new AccessibilityButtonCallback() {
        @Override
        public void onClicked(final AccessibilityButtonController controller) {
            startActivity(Apps.meTask(EmillaAccessibilityService.this, AssistActivity.class));
            // todo: could instead invoke the generic "assist" action, by KeyEvent or otherwise
            //  not that i'm pining to give the other assistants an upper hand, but that's the
            //  exact sort of lock-in we're against, right? ;)
        }

//            @Override
//            public void onAvailabilityChanged(final AccessibilityButtonController controller,
//                    final boolean available) {}
    };
    controller.registerAccessibilityButtonCallback(mCallback, new Handler());
}}
}
