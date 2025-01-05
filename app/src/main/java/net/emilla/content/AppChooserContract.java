package net.emilla.content;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;

public class AppChooserContract extends ResultContract<Intent, ActivityResult, AppChoiceReceiver> {

    public AppChooserContract(AssistActivity act) {
        super(act, new StartActivityForResult());
    }

    public void retrieve(AppChoiceReceiver receiver, Intent target, @StringRes int title) {
        if (alreadyHas(receiver)) return;

        AppChoiceRetriever.setContract(this);

        target.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent chooser;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            int flags = PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT;
            IntentSender sender = PendingIntent.getBroadcast(activity, 0,
                    new Intent(activity, AppChoiceRetriever.class), flags).getIntentSender();

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
            AppChoiceRetriever.deleteContract();
        }
    }
}
