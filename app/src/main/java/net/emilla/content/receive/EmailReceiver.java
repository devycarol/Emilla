package net.emilla.content.receive;

import static net.emilla.contact.adapter.ContactEmailAdapter.INDEX_ADDRESS;

import android.database.Cursor;

import net.emilla.activity.AssistActivity;

@FunctionalInterface
public interface EmailReceiver extends ContactDataReceiver {

    @Override
    default void useContact(AssistActivity act, Cursor cur) {
        provide(act, cur.getString(INDEX_ADDRESS));
    }

    /// Provides the receiver with an email address.
    ///
    /// @param emailAddress is provided to the receiver.
    @Override
    void provide(AssistActivity act, String emailAddress);

}
