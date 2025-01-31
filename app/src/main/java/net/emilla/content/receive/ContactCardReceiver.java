package net.emilla.content.receive;

import static net.emilla.contact.adapter.ContactCursorAdapter.IDX_ID;
import static net.emilla.contact.adapter.ContactCursorAdapter.IDX_KEY;

import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;

public interface ContactCardReceiver extends ContactReceiver {

    @Override
    default void useContact(Cursor cur) {
        long id = cur.getLong(IDX_ID);
        String key = cur.getString(IDX_KEY);

        provide(ContactsContract.Contacts.getLookupUri(id, key));
    }

    /**
     * @param contact is provided to the object.
     */
    void provide(@NonNull Uri contact);
}
