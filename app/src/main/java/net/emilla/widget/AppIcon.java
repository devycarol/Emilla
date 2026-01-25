package net.emilla.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;

public final class AppIcon implements ActionIcon {
    private final Drawable mDrawable;

    public AppIcon(Drawable drawable) {
        mDrawable = drawable;
    }

    @Override
    public Drawable drawable(Context ctx) {
        return mDrawable;
    }
}
