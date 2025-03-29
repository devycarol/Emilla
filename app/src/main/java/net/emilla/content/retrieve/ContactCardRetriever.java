package net.emilla.content.retrieve;

import android.net.Uri;

import androidx.activity.result.contract.ActivityResultContracts.PickContact;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.content.receive.ContactCardReceiver;

public final class ContactCardRetriever extends ResultRetriever<Void, Uri, ContactCardReceiver> {

    public ContactCardRetriever(AssistActivity act) {
        super(act, new PickContact());
    }

    public void retrieve(ContactCardReceiver receiver) {
        if (alreadyHas(receiver)) return;
        launch(null);
    }

    @Override
    protected ResultCallback makeCallback() {
        return new ContactCallback();
    }

    private /*inner*/ final class ContactCallback extends ResultCallback {

        @Override
        protected void onActivityResult(Uri contact, ContactCardReceiver receiver) {
            if (contact != null) {
                activity.suppressResumeChime();
                receiver.provide(contact);
            } else activity.toast(R.string.toast_contact_not_selected);
        }
    }
}
