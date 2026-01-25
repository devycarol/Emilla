package net.emilla.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;

public sealed interface ActionIcon permits SymbolIcon, AppIcon {
    Drawable drawable(Context ctx);
}
