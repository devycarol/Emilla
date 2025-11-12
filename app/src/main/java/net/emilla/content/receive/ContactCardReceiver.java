package net.emilla.content.receive;

import static net.emilla.contact.adapter.ContactCursorAdapter.INDEX_ID;
import static net.emilla.contact.adapter.ContactCursorAdapter.INDEX_KEY;

import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import net.emilla.activity.AssistActivity;

@FunctionalInterface
public interface ContactCardReceiver extends ContactReceiver {

    @Override
    default void useContact(AssistActivity act, Cursor cur) {
        long id = cur.getLong(INDEX_ID);
        String key = cur.getString(INDEX_KEY);

        provide(act, ContactsContract.Contacts.getLookupUri(id, key));
    }

    /// Provides the receiver with a contact.
    ///
    /// @param contact is provided to the receiver.
    void provide(AssistActivity act, Uri contact);

}
