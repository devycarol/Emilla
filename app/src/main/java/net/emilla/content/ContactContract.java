package net.emilla.content;

import android.net.Uri;

import androidx.activity.result.contract.ActivityResultContracts.PickContact;

import net.emilla.AssistActivity;
import net.emilla.R;

public class ContactContract extends ResultContract<Void, Uri, ContactReceiver> {

    public ContactContract(AssistActivity act) {
        super(act, new PickContact());
    }

    public void retrieve(ContactReceiver receiver) {
        if (alreadyHas(receiver)) return;
        launcher.launch(null);
    }

    @Override
    protected ResultCallback makeCallback() {
        return new ContactCallback();
    }

    private class ContactCallback extends ResultCallback {

        @Override
        protected void onActivityResult(Uri contact, ContactReceiver receiver) {
            if (contact != null) receiver.provide(contact);
            else activity.toast(R.string.toast_contact_not_selected);
        }
    }
}
