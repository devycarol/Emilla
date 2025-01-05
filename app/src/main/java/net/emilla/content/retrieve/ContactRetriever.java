package net.emilla.content.retrieve;

import android.net.Uri;

import androidx.activity.result.contract.ActivityResultContracts.PickContact;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.content.receive.ContactReceiver;

public class ContactRetriever extends ResultRetriever<Void, Uri, ContactReceiver> {

    public ContactRetriever(AssistActivity act) {
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
