package net.emilla.content;

import android.net.Uri;
import android.util.Log;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.PickContact;

import net.emilla.EmillaActivity;
import net.emilla.R;

public class ContactContract {

    private static final String TAG = ContactContract.class.getSimpleName();

    private final EmillaActivity mActivity;
    private final ActivityResultLauncher<Void> mLauncher;
    private ContactReceiver mReceiver;

    public ContactContract(EmillaActivity act) {
        mActivity = act;
        mLauncher = act.registerForActivityResult(new PickContact(), new ContactCallback());
    }

    public void retrieve(ContactReceiver receiver) {
        if (mReceiver != null) {
            Log.d(TAG, "retrieve: result launcher already engaged. Not launching again.");
            return;
        }

        mReceiver = receiver;
        mLauncher.launch(null);
    }

    private class ContactCallback implements ActivityResultCallback<Uri> {

        @Override
        public void onActivityResult(Uri contact) {
            if (contact != null) mReceiver.provide(contact);
            else mActivity.toast(R.string.toast_contact_not_selected);
            mReceiver = null;
        }
    }
}
