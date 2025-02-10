package net.emilla.contact.adapter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;
import android.view.View;

import net.emilla.contact.ContactItemView;

public class ContactCardAdapter extends ContactCursorAdapter {

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
        final var item = (ContactItemView) view;
        item.setContactInfo(cur.getLong(IDX_ID), cur.getString(IDX_KEY), cur.getString(IDX_NAME),
                cur.getString(IDX_PHOTO), cur.getInt(IDX_STARRED) != 0);
        item.setSelected(true);
    }
}
