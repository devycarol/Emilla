package net.emilla.contact.adapter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.RawContacts;
import android.view.View;

import net.emilla.contact.ContactItemView;
import net.emilla.lang.Lang;

public final class ContactEmailAdapter extends ContactCursorAdapter {

    private static final String[] ADD_COLS = {
            RawContacts.CONTACT_ID,
            Email.ADDRESS,
            Email.TYPE,
            Email.LABEL,
    };

    private static final String[] PROJECTION = projection(ADD_COLS);

    public static final int IDX_CONTACT_ID = PROJECTION.length - 4;
    public static final int IDX_ADDRESS = PROJECTION.length - 3;
    public static final int IDX_TYPE = PROJECTION.length - 2;
    public static final int IDX_LABEL = PROJECTION.length - 1;

    public ContactEmailAdapter(Context ctx) {
        super(ctx);
    }

    @Override
    public Uri contentUri() {
        return deduplicate(Email.CONTENT_URI);
    }

    @Override
    public Uri filterUri(String search) {
        return deduplicateFilter(Email.CONTENT_FILTER_URI, search);
    }

    @Override
    public String[] projection() {
        return PROJECTION;
    }

    @Override
    public void bindView(View view, Context ctx, Cursor cur) {
        var item = (ContactItemView) view;
        item.setContactInfo(cur.getLong(IDX_CONTACT_ID), cur.getString(IDX_KEY),
                cur.getString(IDX_NAME), cur.getString(IDX_PHOTO), cur.getInt(IDX_STARRED) != 0);

        int type = cur.getInt(IDX_TYPE);
        var customLabel = cur.getString(IDX_LABEL);

        CharSequence typeText = Email.getTypeLabel(this.resources, type, customLabel);
        var address = cur.getString(IDX_ADDRESS);

        item.setContactDetail(Lang.colonConcat(this.resources, typeText, address));
    }
}
