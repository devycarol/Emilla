package net.emilla.content;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AppChoiceRetriever extends BroadcastReceiver {

    @Deprecated
    private static AppChooserContract sContract;
    // Todo: there *has* to be a better way.

    @Deprecated
    public static void setContract(AppChooserContract contract) {
        sContract = contract;
    }

    @Deprecated
    public static void deleteContract() {
        sContract = null;
    }

    @Override @Deprecated
    public void onReceive(Context context, Intent intent) {
        // Because Intent.createChooser(Intent, CharSequence, IntentSender) requires API 22, this
        // will not occur on devices older than Lollipop MR1.
        if (sContract == null) return;
        AppChoiceReceiver receiver = sContract.receiver();
        if (receiver != null) {
            receiver.provide(true);
            // There's an edge case where, if the chooser has just one app and launches immediately,
            // this is called immediately after the activity does its manual 'pend' chime. This
            // results in a double-chime when provide(true) triggers a chime.
            sContract.deleteReceiver();
        }
        sContract = null;
    }
}
