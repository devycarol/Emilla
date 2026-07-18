package net.emilla.contact.fragment;

import static net.emilla.contact.adapter.ContactEmailAdapter.INDEX_ADDRESS;

import android.database.Cursor;
import android.widget.ListView;

import androidx.annotation.Nullable;

import net.emilla.contact.adapter.ContactCursorAdapter;
import net.emilla.contact.adapter.ContactEmailAdapter;

public final class EmailFragment extends ContactsFragment<String> {
    public static EmailFragment newInstance() {
        return newInstance(new EmailFragment(), true);
    }

    @Override
    protected ContactCursorAdapter cursorAdapter() {
        return new ContactEmailAdapter(requireContext());
    }

    @Override @Nullable
    protected String selectedContactsInternal(ListView contactList, Cursor cur) {
        return multiSelectedCsv(contactList, cur, INDEX_ADDRESS);
    }

    @Nullable
    public String subject() {
        // Todo
        return null;
    }
}
