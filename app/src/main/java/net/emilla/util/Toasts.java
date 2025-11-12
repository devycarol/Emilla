package net.emilla.util;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.StringRes;

public final class Toasts {

    public static void show(Context ctx, @StringRes int text) {
        Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
    }

    public static void show(Context ctx, CharSequence text) {
        show(ctx, text, false);
    }

    public static void show(Context ctx, CharSequence text, boolean isLongToast) {
        Toast.makeText(ctx, text, isLongToast ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    private Toasts() {}

}
