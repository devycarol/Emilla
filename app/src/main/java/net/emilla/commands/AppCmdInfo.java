package net.emilla.commands;

import static net.emilla.utils.Apps.*;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;

public class AppCmdInfo {
public static final String CLS_MARKOR_MAIN = "net.gsantner.markor.activity.MainActivity";

final CharSequence label;
final String pkg;
final String cls;
final boolean has_send;
final boolean basic;

AppCmdInfo(final ActivityInfo info, final PackageManager pm, final CharSequence appLabel) {
    label = appLabel;
    pkg = info.packageName;
    cls = info.name;
    has_send = sendTask(pkg).resolveActivity(pm) != null;
    basic = switch (pkg) {
        case PKG_AOSP_CONTACTS, PKG_FIREFOX, PKG_YOUTUBE -> false;
        case PKG_TOR -> true; // Search/send intents are broken
        case PKG_MARKOR -> !cls.equals(CLS_MARKOR_MAIN);
        // Markor can have multiple launchers. Only the main one should have the 'send' property.
        default -> !has_send;
    };
    // TODO: just have a generic implementation of AppSearchCommand, this above is risky.
}
}
