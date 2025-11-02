package net.emilla.command.core;

import static android.content.Intent.ACTION_CALL;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.Nullable;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.apps.Apps;
import net.emilla.contact.fragment.ContactPhonesFragment;
import net.emilla.content.receive.PhoneReceiver;
import net.emilla.util.Contacts;
import net.emilla.util.Dialogs;
import net.emilla.util.Features;
import net.emilla.util.Permissions;

/*internal*/ final class Call extends CoreCommand implements PhoneReceiver {

    public static final String ENTRY = "call";

    public static boolean possible(PackageManager pm) {
        return Features.phone(pm) || Apps.canDo(pm, makeIntent(""));
    }

    private ContactPhonesFragment mContactsFragment = null;

    /*internal*/ Call(AssistActivity act) {
        super(act, CoreEntry.CALL, EditorInfo.IME_ACTION_GO);
    }

    @Override
    protected void onInit() {
        super.onInit();

        if (mContactsFragment == null) {
            mContactsFragment = ContactPhonesFragment.newInstance(false);
        }

        this.activity.giveActionBox(mContactsFragment);
    }

    @Override
    protected void onInstruct(@Nullable String instruction) {
        super.onInstruct(instruction);
        mContactsFragment.search(instruction);
    }

    @Override
    protected void onClean() {
        super.onClean();

        this.activity.removeActionBox(mContactsFragment);
        mContactsFragment = null;
    }

    @Override
    protected void run() {
        Permissions.withContacts(this.activity, this::tryCall);
    }

    private void tryCall() {
        String number = mContactsFragment.selectedContacts();
        if (number != null) {
            call(number);
        } else {
            this.activity.offerContactPhones(this);
        }
    }

    @Override
    protected void run(String nameOrNumber) {
        // todo: conference calls?
        if (!Features.phone(pm())) throw badCommand(R.string.error_feature_phone);
        // Todo: handle at install - make sure it's not 'sticky' in sharedprefs in case of data
        //  transfer. it shouldn't disable the "command enabled" pref, it should just be its own
        //  element of an "is the command enabled" check similar to HeliBoard's handling in its
        //  "SettingsValues" class.
        Permissions.withCall(this.activity, () -> tryCall(nameOrNumber));
    }

    private void tryCall(String nameOrNumber) {
        String number = mContactsFragment.selectedContacts();

        if (number == null && Contacts.isPhoneNumbers(nameOrNumber)) {
            number = nameOrNumber;
        }

        if (number != null) {
            call(number);
        } else {
            String msg = str(
                R.string.notice_call_not_number,
                nameOrNumber,
                Contacts.phonewordsToNumbers(nameOrNumber)
            );
            offerDialog(
                Dialogs.dual(
                    this.activity,
                    CoreEntry.CALL.name,
                    msg, R.string.call_directly,

                    (dlg, which) -> call(nameOrNumber)
                )
            );
        }
    }

    private void call(String nameOrNumber) {
        this.activity.suppressSuccessChime();
        appSucceed(makeIntent(nameOrNumber));
    }

    private static Intent makeIntent(String number) {
        return new Intent(ACTION_CALL, Uri.parse("tel:" + number));
    }

    @Override
    public void provide(String phoneNumber) {
        Permissions.withCall(this.activity, () -> call(phoneNumber));
    }

}
