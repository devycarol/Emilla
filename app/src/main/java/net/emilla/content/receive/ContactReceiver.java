package net.emilla.content.receive;

import android.database.Cursor;

public interface ContactReceiver extends ResultReceiver {

    void useContact(Cursor cur);
}
