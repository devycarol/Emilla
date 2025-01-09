package net.emilla.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityManager;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat;

import net.emilla.R;
import net.emilla.action.QuickAction;

public class ActionButton extends AppCompatImageButton implements View.OnTouchListener {

    private boolean mHasLongPress = false;
    private boolean mLongTouching = false;
    private Drawable mIcon;
    private Drawable mLongIcon;
    private boolean mHasAppIcon = false;
    private final Drawable mBackground;

    private final Runnable onLongPress = () -> {
        mLongTouching = true;
        performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        setImageDrawable(mLongIcon);
        if (mHasAppIcon) applyNormalIconBackground(getResources());
    };

    public ActionButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setOnTouchListener(this);
        mIcon = getDrawable();
        mBackground = getBackground();
    }

    public final void setIcon(@DrawableRes int icon) {
        setIcon(AppCompatResources.getDrawable(getContext(), icon), false);
    }

    public final void setIcon(Drawable icon, boolean isAppIcon) {
        setImageDrawable(mIcon = icon);
        if (mHasAppIcon != isAppIcon) {
            mHasAppIcon = isAppIcon;
            Resources res = getResources();
            if (isAppIcon) applyAppIconBackground(res);
            else applyNormalIconBackground(res);
        }
    }

    public void setLongPress(QuickAction action, Resources res) {
        setOnLongClickListener(v -> {
            action.perform();
            return true;
        });

        mLongIcon = AppCompatResources.getDrawable(getContext(), action.icon());

        ViewCompat.replaceAccessibilityAction(this, AccessibilityActionCompat.ACTION_LONG_CLICK,
                action.description(res), null);

        mHasLongPress = true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        AccessibilityManager am = (AccessibilityManager) getContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (!am.isTouchExplorationEnabled()) switch (event.getAction()) {
            // do not perform special behavior if a screen reader is in use.
            case MotionEvent.ACTION_DOWN -> {
                setPressed(true);
                performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                if (mHasLongPress) postDelayed(onLongPress, ViewConfiguration.getLongPressTimeout());
                return true;
            }
            case MotionEvent.ACTION_MOVE -> {
                boolean pressed = isPressed();
                super.onTouchEvent(event);
                if (isPressed() != pressed) {
                    if (mLongTouching) {
                        performHapticFeedback(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                                ? HapticFeedbackConstants.GESTURE_END : HapticFeedbackConstants.KEYBOARD_TAP);
                        resetIcon();
                        mLongTouching = false;
                    } else if (mHasLongPress) getHandler().removeCallbacks(onLongPress);
                }
                return true;
            }
            case MotionEvent.ACTION_UP -> {
                if (mHasLongPress && isPressed()) {
                    setPressed(false);
                    if (mLongTouching) {
                        mLongTouching = false;
                        resetIcon();
                        performLongClick();
                    } else {
                        if (mHasLongPress) getHandler().removeCallbacks(onLongPress);
                        playSoundEffect(SoundEffectConstants.CLICK);
                        callOnClick();
                    }
                    cancelLongPress();
                    return true;
                }
            }
        }
        return super.onTouchEvent(event);
    }

    private void resetIcon() {
        setImageDrawable(mIcon);
        if (mHasAppIcon) applyAppIconBackground(getResources());
    }

    private void applyAppIconBackground(Resources res) {
        setBackgroundColor(res.getColor(android.R.color.transparent));
        // Todo: make the app icon have a ripple highlight effect.
        setPadding(0, 0, 0, 0);
    }

    private void applyNormalIconBackground(Resources res) {
        setBackgroundDrawable(mBackground);
        int pad = res.getDimensionPixelSize(R.dimen.margin_narrow);
        setPadding(pad, pad, pad, pad);
    }
}
