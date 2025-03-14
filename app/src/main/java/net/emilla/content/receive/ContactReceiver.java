package net.emilla.content.receive;

import android.database.Cursor;

@FunctionalInterface
public interface ContactReceiver extends ResultReceiver {

    void useContact(Cursor cur);
}
