package net.emilla.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;
import androidx.appcompat.content.res.AppCompatResources;

import org.jspecify.annotations.Nullable;

import java.util.Objects;

public final class SymbolIcon implements ActionIcon {
    @DrawableRes
    private final int mIcon;
    @Nullable
    private Drawable mDrawable = null;

    public SymbolIcon(@DrawableRes int icon) {
        mIcon = icon;
    }

    public SymbolIcon(Drawable drawable) {
        this(0);
        mDrawable = drawable;
    }

    @Override
    public Drawable drawable(Context ctx) {
        if (mDrawable == null) {
            mDrawable = Objects.requireNonNull(
                AppCompatResources.getDrawable(ctx, mIcon)
            );
        }
        return mDrawable;
    }
}
