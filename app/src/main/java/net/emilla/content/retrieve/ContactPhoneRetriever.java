package net.emilla.content.retrieve;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import androidx.annotation.Nullable;

import net.emilla.AssistActivity;
import net.emilla.util.Contacts;

public final class ContactPhoneRetriever extends ContactDataRetriever {

    private static final class PickContactPhone extends PickContactData {

        PickContactPhone(Context ctx) {
            super(ctx);
        }

        @Override
        protected String contentType() {
            return Phone.CONTENT_TYPE;
        }

        @Override @Nullable
        protected String parseData(Uri contact, ContentResolver cr) {
            return Contacts.phoneNumber(contact, cr);
        }
    }

    public ContactPhoneRetriever(AssistActivity act) {
        super(act, new PickContactPhone(act));
    }
}
