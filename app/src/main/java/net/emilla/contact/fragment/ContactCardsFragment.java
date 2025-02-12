package net.emilla.contact.fragment;

import static net.emilla.contact.adapter.ContactCursorAdapter.IDX_ID;
import static net.emilla.contact.adapter.ContactCursorAdapter.IDX_KEY;

import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;
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
            return Contacts.getLookupUri(cur.getLong(IDX_ID), cur.getString(IDX_KEY));
        }

        int pos = contactList.getCheckedItemPosition();
        if (cur.moveToPosition(pos)) return Contacts.getLookupUri(cur.getLong(IDX_ID),
                cur.getString(IDX_KEY));

        return null;
    }
}
