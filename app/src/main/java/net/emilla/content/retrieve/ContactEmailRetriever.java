package net.emilla.content.retrieve;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Email;

import androidx.annotation.Nullable;

import net.emilla.AssistActivity;
import net.emilla.util.Contacts;

public final class ContactEmailRetriever extends ContactDataRetriever {

    private static final class PickContactEmail extends PickContactData {

        public PickContactEmail(Context ctx) {
            super(ctx);
        }

        @Override
        protected String contentType() {
            return Email.CONTENT_TYPE;
        }

        @Override @Nullable
        protected String parseData(Uri contact, ContentResolver cr) {
            return Contacts.emailAddress(contact, cr);
        }
    }

    public ContactEmailRetriever(AssistActivity act) {
        super(act, new PickContactEmail(act));
    }
}
