package net.emilla.content.retrieve;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.Nullable;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.content.receive.ContactDataReceiver;

public abstract class ContactDataRetriever extends ResultRetriever<Void, String, ContactDataReceiver> {

    protected static abstract class PickContactData extends ActivityResultContract<Void, String> {

        private final Context mContext;

        public PickContactData(Context ctx) {
            mContext = ctx;
        }

        @Override
        public final Intent createIntent(Context ctx, Void unused) {
            return new Intent(Intent.ACTION_PICK).setType(contentType());
        }

        protected abstract String contentType();

        @Override @Nullable
        public final String parseResult(int resultCode, @Nullable Intent intent) {
            if (resultCode == Activity.RESULT_OK && intent != null) {
                Uri contact = intent.getData();
                if (contact != null) return parseData(contact, mContext.getContentResolver());
            }
            return null;
        }

        @Nullable
        protected abstract String parseData(Uri contact, ContentResolver cr);
    }

    protected ContactDataRetriever(AssistActivity act, PickContactData contract) {
        super(act, contract);
    }

    public final void retrieve(ContactDataReceiver receiver) {
        if (alreadyHas(receiver)) return;
        launch(null);
    }

    @Override
    protected final ResultCallback makeCallback() {
        return new DataCallback();
    }

    private /*inner*/ final class DataCallback extends ResultCallback {

        @Override
        protected void onActivityResult(String data, ContactDataReceiver receiver) {
            if (data != null) {
                pActivity.suppressResumeChime();
                receiver.provide(data);
            } else pActivity.toast(R.string.toast_contact_not_selected);
        }
    }
}
