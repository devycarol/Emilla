package net.emilla.content.retrieve;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import net.emilla.content.receive.AppChoiceReceiver;

public class AppChoiceBroadcastReceiver extends BroadcastReceiver {

    @Deprecated
    private static AppChoiceRetriever sRetriever;
    // Todo: there *has* to be a better way.

    @Deprecated
    public static void setRetriever(AppChoiceRetriever retriever) {
        sRetriever = retriever;
    }

    @Deprecated
    public static void deleteRetriever() {
        sRetriever = null;
    }

    @Override @Deprecated
    public void onReceive(Context context, Intent intent) {
        // Because Intent.createChooser(Intent, CharSequence, IntentSender) requires API 22, this
        // will not occur on devices older than Lollipop MR1.
        if (sRetriever == null) return;
        AppChoiceReceiver receiver = sRetriever.receiver();
        if (receiver != null) {
            receiver.provide(true);
            // There's an edge case where, if the chooser has just one app and launches immediately,
            // this is called immediately after the activity does its manual 'pend' chime. This
            // results in a double-chime when provide(true) triggers a chime.
            sRetriever.deleteReceiver();
        }
        sRetriever = null;
    }
}
