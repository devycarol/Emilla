package net.emilla.event;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import net.emilla.util.Services;

public abstract class EventScheduler<P extends Plan> {

    protected final Context context;
    private final AlarmManager mAlarmManager;

    public EventScheduler(Context ctx) {
        this.context = ctx;
        mAlarmManager = Services.alarm(ctx);
    }

    public final void plan(P plan) {
        PendingIntent pendingIntent = pendingIntentFor(plan);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || mAlarmManager.canScheduleExactAlarms()) {
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, plan.time, pendingIntent);
        } else {
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, plan.time, pendingIntent);
        }
        // Todo: communicate which will happen in the settings.
    }

    public final void cancel(P plan) {
        mAlarmManager.cancel(pendingIntentFor(plan));
    }

    private PendingIntent pendingIntentFor(P plan) {
        int flags = PendingIntent.FLAG_UPDATE_CURRENT
                  | PendingIntent.FLAG_IMMUTABLE;
        return PendingIntent.getBroadcast(this.context, plan.slot, intentFor(plan), flags);
    }

    protected abstract Intent intentFor(P plan);
}
