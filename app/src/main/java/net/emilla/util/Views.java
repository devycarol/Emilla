package net.emilla.util;

import static androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK;

import android.content.res.Resources;
import android.view.View;

import androidx.annotation.StringRes;
import androidx.core.view.ViewCompat;

import net.emilla.lang.Lang;

public final class Views {

    public static void setStateDescriptionCompat(
        Resources res,
        View view,
        CharSequence content,
        @StringRes int state
    ) {
        view.setContentDescription(Lang.commaConcat(res, content, state));
    }

    public static void removeStateDescriptionCompat(View view, CharSequence contentDescription) {
        view.setContentDescription(contentDescription);
    }

    public static void setClickActionLabel(Resources res, View view, @StringRes int label) {
        ViewCompat.replaceAccessibilityAction(view, ACTION_CLICK, res.getString(label), null);
    }

    private Views() {}

}
