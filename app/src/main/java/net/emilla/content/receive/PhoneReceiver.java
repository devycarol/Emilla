package net.emilla.content.receive;

import static net.emilla.contact.adapter.ContactPhoneAdapter.INDEX_NUMBER;

import android.database.Cursor;

import net.emilla.activity.AssistActivity;

@FunctionalInterface
public interface PhoneReceiver extends ContactDataReceiver {

    @Override
    default void useContact(AssistActivity act, Cursor cur) {
        provide(act, cur.getString(INDEX_NUMBER));
    }

    /// Provides the receiver with a phone number.
    ///
    /// @param phoneNumber is provided to the receiver.
    @Override
    void provide(AssistActivity act, String phoneNumber);

}
