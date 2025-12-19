package net.emilla.content.retrieve;

import static net.emilla.chime.Chime.RESUME;

import android.net.Uri;

import androidx.activity.result.contract.ActivityResultContracts.PickContact;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.inner;
import net.emilla.content.receive.ContactCardReceiver;
import net.emilla.util.Toasts;

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

    private @inner final class ContactCallback extends ResultCallback {

        @Override
        protected void onActivityResult(Uri contact, ContactCardReceiver receiver) {
            AssistActivity act = ContactCardRetriever.this.activity;
            if (contact != null) {
                act.suppressChime(RESUME);
                receiver.provide(act, contact);
            } else {
                Toasts.show(act, R.string.toast_contact_not_selected);
            }
        }
    }

}
