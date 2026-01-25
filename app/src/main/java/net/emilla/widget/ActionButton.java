package net.emilla.widget;

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

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat;

import net.emilla.R;
import net.emilla.action.QuickAction;
import net.emilla.util.Services;

public final class ActionButton extends AppCompatImageButton implements View.OnTouchListener {
    private boolean mHasLongPress = false;
    private boolean mLongTouching = false;
    private ActionIcon mIcon;
    private ActionIcon mLongIcon = null;
    private final Drawable mBackground;

    private final Runnable mOnLongPress = () -> {
        mLongTouching = true;
        performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        applyIcon(mLongIcon);
    };

    public ActionButton(Context ctx, @Nullable AttributeSet attrs) {
        super(ctx, attrs);

        setOnTouchListener(this);
        mIcon = new SymbolIcon(getDrawable());
        mBackground = getBackground();
    }

    public void setIcon(ActionIcon icon) {
        mIcon = icon;
        applyIcon(mIcon);
    }

    public void setLongPress(QuickAction action, Resources res) {
        setOnLongClickListener(view -> {
            action.perform();
            return true;
        });

        mLongIcon = action.icon();

        ViewCompat.replaceAccessibilityAction(
            this,
            AccessibilityActionCompat.ACTION_LONG_CLICK,
            action.description(res),
            null
        );

        mHasLongPress = true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        AccessibilityManager am = Services.accessibility(getContext());
        if (!am.isTouchExplorationEnabled()) switch (event.getAction()) {
            // do not perform special behavior if a screen reader is in use.
            case MotionEvent.ACTION_DOWN -> {
                setPressed(true);
                performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);

                if (mHasLongPress) {
                    postDelayed(mOnLongPress, ViewConfiguration.getLongPressTimeout());
                }

                return true;
            }
            case MotionEvent.ACTION_MOVE -> {
                boolean pressed = isPressed();
                onTouchEvent(event);
                if (isPressed() != pressed) {
                    if (mLongTouching) {
                        performHapticFeedback(
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                                ? HapticFeedbackConstants.GESTURE_END
                                : HapticFeedbackConstants.KEYBOARD_TAP
                        );
                        applyIcon(mIcon);
                        mLongTouching = false;
                    } else if (mHasLongPress) {
                        getHandler().removeCallbacks(mOnLongPress);
                    }
                }
                return true;
            }
            case MotionEvent.ACTION_UP -> {
                if (mHasLongPress && isPressed()) {
                    setPressed(false);
                    if (mLongTouching) {
                        mLongTouching = false;
                        applyIcon(mIcon);
                        performLongClick();
                    } else {
                        if (mHasLongPress) {
                            getHandler().removeCallbacks(mOnLongPress);
                        }

                        playSoundEffect(SoundEffectConstants.CLICK);
                        callOnClick();
                    }
                    cancelLongPress();
                    return true;
                }
            }
        }
        return onTouchEvent(event);
    }

    private void applyIcon(ActionIcon icon) {
        setImageDrawable(icon.drawable(getContext()));
        var res = getResources();
        int pad;
        if (icon instanceof AppIcon) {
            setBackgroundColor(res.getColor(android.R.color.transparent));
            // Todo: make the app icon have a ripple highlight effect.
            pad = 0;
        } else {
            setBackgroundDrawable(mBackground);
            pad = res.getDimensionPixelSize(R.dimen.action_button_padding);
        }
        setPadding(pad, pad, pad, pad);
    }
}
