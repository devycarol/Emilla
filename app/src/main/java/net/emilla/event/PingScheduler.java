package net.emilla.event;

import android.content.Context;
import android.content.Intent;

import net.emilla.ping.PingIntent;

public final class PingScheduler extends EventScheduler<PingPlan> {

    public PingScheduler(Context ctx) {
        super(ctx);
    }

    @Override
    protected Intent intentFor(PingPlan plan) {
        return new PingIntent(pContext, plan.ping, plan.channel);
    }
}
