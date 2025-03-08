package net.emilla.content.receive;

import static net.emilla.contact.adapter.ContactEmailAdapter.IDX_ADDRESS;

import android.database.Cursor;

public interface EmailReceiver extends ContactDataReceiver {

    @Override
    default void useContact(Cursor cur) {
        provide(cur.getString(IDX_ADDRESS));
    }

    /**
     * @param emailAddress is provided to the object.
     */
    void provide(String emailAddress);
}
