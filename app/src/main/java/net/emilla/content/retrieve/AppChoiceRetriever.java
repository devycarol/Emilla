package net.emilla.content.retrieve;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.content.receive.AppChoiceReceiver;

public class AppChoiceRetriever extends ResultRetriever<Intent, ActivityResult, AppChoiceReceiver> {

    public AppChoiceRetriever(AssistActivity act) {
        super(act, new StartActivityForResult());
    }

    public void retrieve(AppChoiceReceiver receiver, Intent target, @StringRes int title) {
        if (alreadyHas(receiver)) return;

        AppChoiceBroadcastReceiver.setRetriever(this);

        target.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent chooser;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            int flags = PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT;
            IntentSender sender = PendingIntent.getBroadcast(activity, 0,
                    new Intent(activity, AppChoiceBroadcastReceiver.class), flags).getIntentSender();

            chooser = Intent.createChooser(target, activity.getString(title), sender);
        } else chooser = Intent.createChooser(target, activity.getString(title));

        launcher.launch(chooser);
    }

    @Deprecated
    public final AppChoiceReceiver receiver() {
        return super.receiver();
    }

    @Deprecated
    public final void deleteReceiver() {
        super.deleteReceiver();
    }

    @Override
    protected ResultCallback makeCallback() {
        return new AppCallback();
    }

    private class AppCallback extends ResultCallback {

        @Override
        protected void onActivityResult(ActivityResult output, AppChoiceReceiver receiver) {
            if (receiver == null) return;
            receiver.provide(false);
            AppChoiceBroadcastReceiver.deleteRetriever();
        }
    }
}
