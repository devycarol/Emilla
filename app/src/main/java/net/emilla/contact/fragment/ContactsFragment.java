package net.emilla.contact.fragment;

import static net.emilla.chime.Chimer.RESUME;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.contact.ContactItemView;
import net.emilla.contact.MultiSearch;
import net.emilla.contact.adapter.ContactCursorAdapter;
import net.emilla.content.receive.ContactReceiver;
import net.emilla.util.Apps;
import net.emilla.util.Permissions;

public abstract class ContactsFragment<T> extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private ListView mContactList;
    private LinearLayout mPermissionContainer;
    private Button mPermissionButton;

    private ContactCursorAdapter mCursorAdapter;
    @Nullable
    private String mSearchString;

    private AssistActivity mActivity;
    private boolean mHasMultiSelect;

    public ContactsFragment() {
        super(R.layout.fragment_contacts);
    }

    protected static <F extends ContactsFragment<?>> F newInstance(F base, boolean multiSelect) {
        var args = new Bundle();
        args.putBoolean("multiSelect", multiSelect);
        base.setArguments(args);

        return base;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mHasMultiSelect = requireArguments().getBoolean("multiSelect");

        mContactList = view.findViewById(R.id.ctct_list);
        mPermissionContainer = view.findViewById(R.id.ctct_permission_container);
        mPermissionButton = view.findViewById(R.id.ctct_permission_btn);

        mActivity = (AssistActivity) requireActivity();

        if (Permissions.contacts(requireContext())) populateContacts();
        else deactivateContacts();
    }

    protected final boolean hasMultiSelect() {
        return mHasMultiSelect;
    }

    private void deactivateContacts() {
        mContactList.setVisibility(View.GONE);
        mPermissionContainer.setVisibility(View.VISIBLE);
        mPermissionButton.setOnClickListener(v -> {
            AssistActivity act = (AssistActivity) requireActivity();
            Permissions.withContacts(act, this::activateContacts,
                                     () -> startActivity(Apps.infoTask()),
                                     () -> act.chime(RESUME));
        });
    }

    private void activateContacts() {
        mPermissionContainer.setVisibility(View.GONE);
        mContactList.setVisibility(View.VISIBLE);
        populateContacts();
    }

    private void populateContacts() {
        mCursorAdapter = cursorAdapter();

        if (mHasMultiSelect) mContactList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        else mContactList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        mContactList.setOnItemClickListener(this);
        mContactList.setOnItemLongClickListener(this);

        mContactList.setAdapter(mCursorAdapter);
        LoaderManager.getInstance(this).initLoader(0, null, this);
    }

    protected abstract ContactCursorAdapter cursorAdapter();

    public void search(String search) {
        mSearchString = search;

        Context ctx = getContext();
        if (ctx == null || !Permissions.contacts(ctx)) return;

        LoaderManager.getInstance(this).restartLoader(0, null, this);
    }

    @Override @NonNull
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Uri uri;
        String selection;
        String[] selectionArgs;
        if (mSearchString != null) {
            var baseSelection = Contacts.DISPLAY_NAME_PRIMARY + " LIKE ?";
            if (mHasMultiSelect) {
                var ms = MultiSearch.instance(baseSelection, mSearchString);
                if (ms.hasMultipleTerms()) {
                    return makeLoader(mCursorAdapter.contentUri(), ms.selection(), ms.selectionArgs());
                }
            }

            uri = mCursorAdapter.filterUri(mSearchString);
            selection = baseSelection;
            selectionArgs = new String[]{"%" + mSearchString + "%"};
        } else {
            uri = mCursorAdapter.contentUri();
            selection = null;
            selectionArgs = null;
        }

        return makeLoader(uri, selection, selectionArgs);
    }

    private CursorLoader makeLoader(Uri uri, String selection, String[] selectionArgs) {
        return new CursorLoader(requireContext(),
                uri, mCursorAdapter.projection(),
                selection, selectionArgs,
                Contacts.STARRED + ", " + Contacts.SORT_KEY_PRIMARY + " DESC");
        // Todo: prioritize results that start with the query.
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        var item = (ContactItemView) view;
        if (item.isDoubleTap() && (!mHasMultiSelect || mContactList.getCheckedItemCount() == 0)) {
            // TODO ACC DONTMERGE: replace double-tap with accessibility action when a service is in
            //  use.
            var adapter = (CursorAdapter) parent.getAdapter();
            var cur = adapter.getCursor();
            cur.moveToPosition(pos);

            var receiver = (ContactReceiver) mActivity.command();
            receiver.useContact(cur);
        } else if (mHasMultiSelect) item.markSelected(mContactList.isItemChecked(pos));
        // TODO ACC DONTMERGE: have this feedback for single-selection as well.
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
        if (!mContactList.isItemChecked(pos)) return false;
        mContactList.setItemChecked(pos, false);
        var item = (ContactItemView) view;
        item.markSelected(false);
        return true;
    }

    @Nullable
    public final T selectedContacts() {
        // todo: when there's only one contact, highlight it to indicate it's automatically selected.
        // TODO ACC DONTMERGE: have a more available feedback for users to know which contacts are
        //  selected without having to scour the whole list.
        if (Permissions.contacts(requireContext())) {
            var adapter = (CursorAdapter) mContactList.getAdapter();
            return selectedContactsInternal(mContactList, adapter.getCursor());
        }
        return null;
    }

    @Nullable
    protected abstract T selectedContactsInternal(ListView contactList, Cursor cur);

    @Nullable
    protected final String multiSelectedCsv(ListView contactList, Cursor cur, int colIndex) {
        int count = cur.getCount();

        if (count == 1) {
            cur.moveToFirst();
            return cur.getString(colIndex);
        }

        if (count > 1) {
            if (hasMultiSelect()) {
                SparseBooleanArray selecteds = contactList.getCheckedItemPositions();
                int selectedCount = selecteds.size();
                if (selectedCount == 0) return null;
                int firstPos = selecteds.keyAt(0);
                cur.moveToPosition(firstPos);
                var firstContact = cur.getString(colIndex);

                if (selectedCount == 1) return firstContact;
                var contacts = new StringBuilder(firstContact);

                for (int i = 1; i < selectedCount; ++i) {
                    int pos = selecteds.keyAt(i);
                    cur.moveToPosition(pos);

                    contacts.append(',').append(cur.getString(colIndex));
                }

                return contacts.toString();
            } else {
                int pos = contactList.getCheckedItemPosition();
                if (cur.moveToPosition(pos)) return cur.getString(colIndex);
            }
        }

        return null;
    }
}
