package net.emilla.content.receive;

import static net.emilla.contact.adapter.ContactCursorAdapter.IDX_ID;
import static net.emilla.contact.adapter.ContactCursorAdapter.IDX_KEY;

import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

@FunctionalInterface
public interface ContactCardReceiver extends ContactReceiver {

    @Override
    default void useContact(Cursor cur) {
        long id = cur.getLong(IDX_ID);
        var key = cur.getString(IDX_KEY);

        provide(ContactsContract.Contacts.getLookupUri(id, key));
    }

    /// Provides the receiver with a contact.
    ///
    /// @param contact is provided to the receiver.
    void provide(Uri contact);
}
