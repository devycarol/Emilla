package net.emilla.content;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.util.Log;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.annotation.StringRes;

import net.emilla.EmillaActivity;

public class AppChooserContract {

    private static final String TAG = AppChooserContract.class.getSimpleName();

    private final EmillaActivity mActivity;
    private final ActivityResultLauncher<Intent> mLauncher;
    private AppChoiceReceiver mReceiver;

    public AppChooserContract(EmillaActivity act) {
        mActivity = act;
        mLauncher = act.registerForActivityResult(new StartActivityForResult(), new AppCallback());
    }

    @Deprecated
    public AppChoiceReceiver receiver() {
        return mReceiver;
    }

    @Deprecated
    public void deleteReceiver() {
        mReceiver = null;
    }

    public void retrieve(AppChoiceReceiver receiver, Intent target, @StringRes int title) {
        if (mReceiver != null) {
            Log.d(TAG, "retrieve: result launcher already engaged. Not launching again.");
            return;
        }

        mReceiver = receiver;
        AppChoiceRetriever.setContract(this);

        target.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent chooser;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            int flags = PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT;
            IntentSender sender = PendingIntent.getBroadcast(mActivity, 0,
                    new Intent(mActivity, AppChoiceRetriever.class), flags).getIntentSender();

            chooser = Intent.createChooser(target, mActivity.getString(title), sender);
        } else chooser = Intent.createChooser(target, mActivity.getString(title));

        mLauncher.launch(chooser);
    }

    private class AppCallback implements ActivityResultCallback<ActivityResult> {

        @Override
        public void onActivityResult(ActivityResult result) {
            if (mReceiver == null) return;
            mReceiver.provide(false);
            mReceiver = null;
            AppChoiceRetriever.deleteContract();
        }
    }
}
