package net.emilla.contact.adapter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;
import android.view.View;

import net.emilla.contact.ContactItemView;

public final class ContactCardAdapter extends ContactCursorAdapter {

    public ContactCardAdapter(Context ctx) {
        super(ctx);
    }

    @Override
    public Uri contentUri() {
        return Contacts.CONTENT_URI;
    }

    @Override
    public Uri filterUri(String search) {
        return Contacts.CONTENT_FILTER_URI.buildUpon()
                .appendPath(search)
                .build();
    }

    @Override
    public String[] projection() {
        return BASE_COLS;
    }

    @Override
    public void bindView(View view, Context ctx, Cursor cur) {
        var item = (ContactItemView) view;
        item.setContactInfo(
            cur.getLong(INDEX_ID),
            cur.getString(INDEX_KEY),
            cur.getString(INDEX_NAME),
            cur.getString(INDEX_PHOTO),
            cur.getInt(INDEX_STARRED) != 0
        );
        item.setSelected(true);
    }
}
