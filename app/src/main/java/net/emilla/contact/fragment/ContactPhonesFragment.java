package net.emilla.contact.fragment;

import static net.emilla.contact.adapter.ContactPhoneAdapter.INDEX_NUMBER;

import android.database.Cursor;
import android.widget.ListView;

import androidx.annotation.Nullable;

import net.emilla.contact.adapter.ContactCursorAdapter;
import net.emilla.contact.adapter.ContactPhoneAdapter;

public final class ContactPhonesFragment extends ContactsFragment<String> {

    public static ContactPhonesFragment newInstance(boolean multiSelect) {
        return newInstance(new ContactPhonesFragment(), multiSelect);
    }

    @Override
    protected ContactCursorAdapter cursorAdapter() {
        return new ContactPhoneAdapter(requireContext());
    }

    @Override @Nullable
    protected String selectedContactsInternal(ListView contactList, Cursor cur) {
        return multiSelectedCsv(contactList, cur, INDEX_NUMBER);
    }
}
