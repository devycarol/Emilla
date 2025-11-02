package net.emilla.command.app;

import android.content.pm.PackageManager;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.apps.Apps;

public final class AppActions {

    public static final int FLAG_TASKER         = 0x8;
    public static final int FLAG_SEARCH         = 0x4;
    public static final int FLAG_SEND_MULTILINE = 0x2;
    public static final int FLAGS_SEND          = 0x1 | FLAG_SEND_MULTILINE;

    private static int flags(PackageManager pm, String pkg, @Nullable AppProperties properties) {
        int mask = properties != null ? properties.actionMask : 0;
        int flags = 0;

        int sendMasked = mask & FLAGS_SEND;
        if (sendMasked != 0 && Apps.canDo(pm, Apps.sendToApp(pkg))) {
            flags |= sendMasked;
        }
        if ((mask & FLAG_SEARCH) != 0 && Apps.canDo(pm, Apps.searchToApp(pkg))) {
            flags |= FLAG_SEARCH;
        }

        return flags;
    }

    private final int mFlags;

    public AppActions(PackageManager pm, String pkg, @Nullable AppProperties properties) {
        mFlags = flags(pm, pkg, properties);
    }

    public boolean usesInstruction() {
        return mFlags != 0;
    }

    private boolean hasMultilineSend() {
        return (mFlags & FLAG_SEND_MULTILINE) != 0;
    }

    public boolean hasSend() {
        return (mFlags & FLAGS_SEND) != 0;
    }

    private boolean hasSearch() {
        return (mFlags & FLAG_SEARCH) != 0;
    }

    private boolean isTasker() {
        return (mFlags & FLAG_TASKER) != 0;
    }

    @StringRes
    public int summary() {
        if (hasSend()) {
            return R.string.summary_app_send;
        }
        if (hasSearch()) {
            return R.string.summary_app_search;
        }
        // Todo: allow multiple actions
        return R.string.summary_app;
    }

    @StringRes
    public int manual() {
        if (isTasker()) {
            return R.string.manual_app_tasker;
        }
        if (hasMultilineSend()) {
            return R.string.manual_app_send_data;
        }
        if (hasSend()) {
            return R.string.manual_app_send;
        }
        if (hasSearch()) {
            return R.string.manual_app_search;
        }
        // Todo: allow multiple actions
        return R.string.manual_app;
    }

    public AppCommand defaultCommand(AssistActivity act, AppEntry appEntry) {
        if (hasMultilineSend()) {
            return new AppSendData(act, appEntry);
        }
        if (hasSend()) {
            return new AppSend(act, appEntry);
        }
        if (hasSearch()) {
            return new AppSearch(act, appEntry);
        }
        // Todo: allow multiple actions
        return new AppCommand(act, appEntry);
    }

}
