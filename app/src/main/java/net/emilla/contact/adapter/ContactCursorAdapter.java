package net.emilla.contact.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cursoradapter.widget.CursorAdapter;

import net.emilla.R;

import java.util.Arrays;

public abstract class ContactCursorAdapter extends CursorAdapter {

    protected static final String[] BASE_COLS = {
            Contacts._ID,
            Contacts.LOOKUP_KEY,
            Contacts.DISPLAY_NAME_PRIMARY,
            Contacts.PHOTO_THUMBNAIL_URI,
            Contacts.STARRED,
    };

    public static final int
            IDX_ID = 0,
            IDX_KEY = 1,
            IDX_NAME = 2,
            IDX_PHOTO = 3,
            IDX_STARRED = 4;

    protected static String[] projection(String[] addCols) {
        String[] fromCols = ContactCursorAdapter.BASE_COLS;
        String[] projection = Arrays.copyOf(fromCols, fromCols.length + addCols.length);
        System.arraycopy(addCols, 0, projection, fromCols.length, addCols.length);
        return projection;
    }

    protected static Uri deduplicate(Uri uri) {
        return uri.buildUpon()
                .appendQueryParameter(ContactsContract.REMOVE_DUPLICATE_ENTRIES, "true")
                .build();
    }

    protected static Uri deduplicateFilter(Uri filterUri, String search) {
        return filterUri.buildUpon()
                .appendPath(search)
                .appendQueryParameter(ContactsContract.REMOVE_DUPLICATE_ENTRIES, "true")
                .build();
    }

    protected final Resources resources;
    private final LayoutInflater mInflater;

    protected ContactCursorAdapter(Context ctx) {
        super(ctx, null, 0);

        this.resources = ctx.getResources();
        mInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public abstract Uri contentUri();
    public abstract Uri filterUri(String search);
    public abstract String[] projection();

    @Override
    public View newView(Context ctx, Cursor cur, ViewGroup parent) {
        return mInflater.inflate(R.layout.contact_item, parent, false);
    }
}
