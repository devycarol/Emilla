package net.emilla.content.receive;

import static net.emilla.contact.adapter.ContactEmailAdapter.IDX_ADDRESS;

import android.database.Cursor;

@FunctionalInterface
public interface EmailReceiver extends ContactDataReceiver {

    @Override
    default void useContact(Cursor cur) {
        provide(cur.getString(IDX_ADDRESS));
    }

    /// Provides the receiver with an email address.
    ///
    /// @param emailAddress is provided to the receiver.
    void provide(String emailAddress);
}
