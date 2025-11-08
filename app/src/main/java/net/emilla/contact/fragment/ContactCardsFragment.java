package net.emilla.contact.fragment;

import static net.emilla.contact.adapter.ContactCursorAdapter.INDEX_ID;
import static net.emilla.contact.adapter.ContactCursorAdapter.INDEX_KEY;

import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.widget.ListView;

import androidx.annotation.Nullable;

import net.emilla.contact.adapter.ContactCardAdapter;
import net.emilla.contact.adapter.ContactCursorAdapter;

public final class ContactCardsFragment extends ContactsFragment<Uri> {

    public static ContactCardsFragment newInstance() {
        return newInstance(new ContactCardsFragment(), false);
    }

    @Override
    protected ContactCursorAdapter cursorAdapter() {
        return new ContactCardAdapter(requireContext());
    }

    @Override @Nullable
    protected Uri selectedContactsInternal(ListView contactList, Cursor cur) {
        if (cur.getCount() == 1) {
            cur.moveToFirst();
            return ContactsContract.Contacts.getLookupUri(cur.getLong(INDEX_ID), cur.getString(
                INDEX_KEY));
        }

        int pos = contactList.getCheckedItemPosition();
        return cur.moveToPosition(pos)
            ? ContactsContract.Contacts.getLookupUri(cur.getLong(INDEX_ID), cur.getString(INDEX_KEY))
            : null;
    }
}
