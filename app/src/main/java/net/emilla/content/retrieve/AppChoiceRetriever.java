package net.emilla.content.retrieve;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.emilla.activity.AssistActivity;
import net.emilla.content.receive.AppChoiceReceiver;

public final class AppChoiceRetriever extends ResultRetriever<Intent, ActivityResult, AppChoiceReceiver> {

    public AppChoiceRetriever(AssistActivity act) {
        super(act, new StartActivityForResult());
    }

    public void retrieve(AppChoiceReceiver receiver, Intent target, @StringRes int title) {
        if (alreadyHas(receiver)) return;

        AppChooserBroadcastReceiver.sRetriever = this;

        target.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent chooser;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            int flags = PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT;
            IntentSender sender = PendingIntent.getBroadcast(
                this.activity, 0,

                new Intent(this.activity, AppChooserBroadcastReceiver.class),
                flags
            ).getIntentSender();

            chooser = Intent.createChooser(target, this.activity.getString(title), sender);
        } else {
            chooser = Intent.createChooser(target, this.activity.getString(title));
        }

        launch(chooser);
    }

    @Override @Deprecated
    public AppChoiceReceiver receiver() {
        return super.receiver();
    }

    @Override @Deprecated
    public void deleteReceiver() {
        super.deleteReceiver();
    }

    @Override
    protected ResultCallback makeCallback() {
        return new AppCallback();
    }

    private /*inner*/ final class AppCallback extends ResultCallback {

        @Override
        protected void onActivityResult(ActivityResult output, AppChoiceReceiver receiver) {
            if (receiver == null) return;
            receiver.provide(false);
            AppChooserBroadcastReceiver.deleteRetriever();
        }
    }

    @Deprecated
    public static final class AppChooserBroadcastReceiver extends BroadcastReceiver {

        @Nullable @Deprecated
        private static AppChoiceRetriever sRetriever;
        // Todo: there *has* to be a better way.

        @Deprecated
        public static void deleteRetriever() {
            sRetriever = null;
        }

        @Override
        public void onReceive(Context ctx, Intent intent) {
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
}
