package net.emilla.contact.fragment;

import static net.emilla.contact.adapter.ContactEmailAdapter.IDX_ADDRESS;

import android.database.Cursor;
import android.widget.ListView;

import androidx.annotation.Nullable;

import net.emilla.contact.adapter.ContactCursorAdapter;
import net.emilla.contact.adapter.ContactEmailAdapter;

public final class ContactEmailsFragment extends ContactsFragment<String> {

    public static ContactEmailsFragment newInstance(boolean multiSelect) {
        return newInstance(new ContactEmailsFragment(), multiSelect);
    }

    @Override
    protected ContactCursorAdapter cursorAdapter() {
        return new ContactEmailAdapter(requireContext());
    }

    @Override @Nullable
    protected String selectedContactsInternal(ListView contactList, Cursor cur) {
        return multiSelectedCsv(contactList, cur, IDX_ADDRESS);
    }
}
