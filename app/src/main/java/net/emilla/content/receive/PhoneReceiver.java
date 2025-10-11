package net.emilla.content.receive;

import static net.emilla.contact.adapter.ContactPhoneAdapter.IDX_NUMBER;

import android.database.Cursor;

@FunctionalInterface
public interface PhoneReceiver extends ContactDataReceiver {

    @Override
    default void useContact(Cursor cur) {
        provide(cur.getString(IDX_NUMBER));
    }

    /// Provides the receiver with a phone number.
    ///
    /// @param phoneNumber is provided to the receiver.
    @Override
    void provide(String phoneNumber);
}
