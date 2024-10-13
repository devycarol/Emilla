package net.emilla.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageButton;

public class ActionButton extends AppCompatImageButton implements View.OnTouchListener {
private boolean mHasLongPress = false;
private boolean mLongTouching = false;
private Drawable mIcon;
private Drawable mLongIcon;

private final Runnable onLongPress = () -> {
    mLongTouching = true;
    performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
    setImageDrawable(mLongIcon);
};

public ActionButton(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);

    setOnTouchListener(this);
    mIcon = getDrawable();
}

public void setIcon(@DrawableRes int resId) {
    Drawable d = AppCompatResources.getDrawable(getContext(), resId);
    setImageDrawable(mIcon = d);
}

public void setLongPress(OnLongClickListener l, @DrawableRes int resId) {
    setOnLongClickListener(l);
    mLongIcon = AppCompatResources.getDrawable(getContext(), resId);
    mHasLongPress = true;
}

@Override
public boolean onTouch(View v, MotionEvent event) {
    switch (event.getAction()) {
    case MotionEvent.ACTION_DOWN -> {
        setPressed(true);
        performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
        if (mHasLongPress) postDelayed(onLongPress, ViewConfiguration.getLongPressTimeout());
        // Todo acc: TalkBack handles this.. okay.. but ideally you shouldn't have to wait longer
        //  once you've already done the double-tap-hold or whichever gesture.
        return true;
    }
    case MotionEvent.ACTION_MOVE -> {
        boolean pressed = isPressed();
        super.onTouchEvent(event);
        if (isPressed() != pressed) {
            if (mLongTouching) {
                performHapticFeedback(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                        ? HapticFeedbackConstants.GESTURE_END : HapticFeedbackConstants.KEYBOARD_TAP);
                setImageDrawable(mIcon);
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
                setImageDrawable(mIcon);
                performLongClick();
            } else {
                if (mHasLongPress) getHandler().removeCallbacks(onLongPress);
                playSoundEffect(SoundEffectConstants.CLICK);
                callOnClick();
            }
            cancelLongPress();
            return true;
        }
    }}
    return super.onTouchEvent(event);
}
}
