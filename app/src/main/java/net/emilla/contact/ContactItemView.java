package net.emilla.contact;

import static androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract.Contacts;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat;
import androidx.core.view.accessibility.AccessibilityViewCommand;

import net.emilla.R;

public final class ContactItemView extends LinearLayout {

    private long mContactId;
    private String mLookupKey;
    private boolean mStarred = false;
    private boolean mSelected = false;

    private ImageView mPhoto;
    private CheckBox mStar;

    private AccessibilityActionCompat mStarAction;
    private final AccessibilityViewCommand mStarCommand = (v, args) -> {
        mStar.toggle();
        return true;
    };

    private long mLastTap = 0;

    public ContactItemView(Context ctx) {
        super(ctx);
    }

    public ContactItemView(Context ctx, @Nullable AttributeSet attrs) {
        super(ctx, attrs);
    }

    public ContactItemView(Context ctx, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(ctx, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mPhoto = findViewById(R.id.ctct_photo);
        findViewById(R.id.ctct_photo_container).setOnClickListener(v -> viewContactDetails());

        mStar = findViewById(R.id.ctct_star);

        var res = getResources();

        ViewCompat.replaceAccessibilityAction(this, ACTION_CLICK, res.getString(R.string.select),
                null);
        ViewCompat.addAccessibilityAction(this, res.getString(R.string.contact_view),
                (v, args) -> {
            viewContactDetails();
            return true;
        });
    }

    public boolean isDoubleTap() {
        long prevTap = mLastTap;
        return (mLastTap = System.currentTimeMillis()) - prevTap < 250;
    }

    private void viewContactDetails() {
        var uri = Contacts.getLookupUri(mContactId, mLookupKey);
        var viewContact = new Intent(Intent.ACTION_VIEW, uri);
        getContext().startActivity(viewContact);
    }

    public void setContactInfo(
        long contactId,
        String lookupKey,
        String name,
        String photoUri,
        boolean starred
    ) {
        mContactId = contactId;
        mLookupKey = lookupKey;

        TextView nameLabel = findViewById(R.id.ctct_name_label);
        nameLabel.setText(name);

        if (photoUri == null) {
            mPhoto.setImageResource(R.drawable.ic_person);
        } else {
            mPhoto.setImageURI(Uri.parse(photoUri));
        }

        setStarred(starred);
    }

    private void setStarred(boolean starred) {
        mStarred = starred;

        mStar.setOnCheckedChangeListener(null);
        mStar.setChecked(starred);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) setStateDesc();

        String label = starActionLabel();
        mStarAction = new AccessibilityActionCompat(R.id.action_toggle_star, label);
        ViewCompat.replaceAccessibilityAction(this, mStarAction, label, mStarCommand);

        mStar.setOnCheckedChangeListener((btn, checked) -> onStarChanged(checked));
    }

    private void onStarChanged(boolean starred) {
        mStarred = starred;

        var values = new ContentValues();
        values.put(Contacts.STARRED, starred ? 1 : 0);

        var ctx = getContext();
        var cr = ctx.getContentResolver();
        String[] selectionArgs = {String.valueOf(mContactId)};
        cr.update(Contacts.CONTENT_URI, values, Contacts._ID + " = ?", selectionArgs);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) setStateDesc();

        ViewCompat.replaceAccessibilityAction(this, mStarAction, starActionLabel(), mStarCommand);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void setStateDesc() {
        var res = getResources();
        if (mSelected) {
            @StringRes int stateDesc = mStarred ? R.string.selected_and_starred : R.string.selected;
            setStateDescription(res.getString(stateDesc));
        } else {
            setStateDescription(mStarred ? res.getString(R.string.starred) : null);
        }
    }

    private String starActionLabel() {
        return getResources().getString(mStarred ? R.string.contact_unstar : R.string.contact_star);
    }

    public void setContactDetail(String detail) {
        TextView detailBox = findViewById(R.id.ctct_detail);
        detailBox.setText(detail);
        detailBox.setVisibility(View.VISIBLE);
    }

    public void markSelected(boolean selected) {
        // todo: have a well-defined behavior for selection retention when search query is updated.
        //  currently, selections seem to be wiped when the search URI changes or when a
        //  search-change other than basic appending (prepending, erasure, ..)
        //  this isn't certain, sometimes erasure does retain selection. i can only hope these
        //  visual retentions are 1:1 with what is actually yielded by `selectedContacts()`
        //  bottom line: enforce a sensible, consistent behavior.
        mSelected = selected;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) setStateDesc();

        String desc = getResources().getString(selected ? R.string.deselect : R.string.select);
        ViewCompat.replaceAccessibilityAction(this, ACTION_CLICK, desc, null);
    }
}
