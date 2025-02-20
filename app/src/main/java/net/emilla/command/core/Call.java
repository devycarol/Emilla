package net.emilla.command.core;

import static android.content.Intent.ACTION_CALL;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.contact.fragment.ContactPhonesFragment;
import net.emilla.content.receive.PhoneReceiver;
import net.emilla.exception.EmlaFeatureException;
import net.emilla.permission.PermissionReceiver;
import net.emilla.settings.Aliases;
import net.emilla.util.Contacts;
import net.emilla.util.Dialogs;
import net.emilla.util.Features;
import net.emilla.util.Permissions;

public final class Call extends CoreCommand implements PhoneReceiver, PermissionReceiver {

    public static final String ENTRY = "call";
    @StringRes
    public static final int NAME = R.string.command_call;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_call;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static Yielder yielder() {
        return new Yielder(true, Call::new, ENTRY, NAME, ALIASES);
    }

    private static final class CallParams extends CoreParams {

        private CallParams() {
            super(NAME,
                  R.string.instruction_phone,
                  R.drawable.ic_call,
                  EditorInfo.IME_ACTION_GO,
                  R.string.summary_call,
                  R.string.manual_call);
        }
    }

    private ContactPhonesFragment mContactsFragment;

    public Call(AssistActivity act) {
        super(act, new CallParams());
    }

    @Override
    protected void onInit() {
        super.onInit();

        if (mContactsFragment == null) mContactsFragment = ContactPhonesFragment.newInstance(false);
        activity.giveActionBox(mContactsFragment);
    }

    @Override
    protected void onInstruct(String instruction) {
        super.onInstruct(instruction);
        mContactsFragment.search(instruction);
    }

    @Override
    protected void onClean() {
        super.onClean();

        activity.removeActionBox(mContactsFragment);
        mContactsFragment = null;
    }

    @Override
    protected void run() {
        if (Permissions.contactsFlow(activity, null)) tryCall();
    }

    private void tryCall() {
        String number = mContactsFragment.selectedContacts();
        if (number != null) call(number);
        else activity.offerContactPhones(this);
    }

    @Override
    protected void run(@NonNull String nameOrNumber) {
        // todo: conference calls?
        if (!Features.phone(pm())) throw new EmlaFeatureException(NAME, R.string.error_feature_phone);
        // Todo: handle at install - make sure it's not 'sticky' in sharedprefs in case of data
        //  transfer. it shouldn't disable the "command enabled" pref, it should just be its own
        //  element of an "is the command enabled" check similar to HeliBoard's handling in its
        //  "SettingsValues" class.
        if (Permissions.callFlow(activity, this)) tryCall(nameOrNumber);
    }

    private void tryCall(@NonNull String nameOrNumber) {
        String number = mContactsFragment.selectedContacts();
        if (number == null && Contacts.isPhoneNumbers(nameOrNumber)) number = nameOrNumber;

        if (number != null) call(number);
        else {
            var msg = string(R.string.notice_call_not_number, nameOrNumber,
                    Contacts.phonewordsToNumbers(nameOrNumber));
            offerDialog(Dialogs.dual(activity, NAME, msg, R.string.call_directly,
                    (dlg, which) -> call(nameOrNumber)));
        }
    }

    private void call(String nameOrNumber) {
        activity.suppressSuccessChime();
        appSucceed(makeIntent(nameOrNumber));
    }

    private static Intent makeIntent(String number) {
        return new Intent(ACTION_CALL, Uri.parse("tel:" + number));
    }

    @Override
    public void provide(@NonNull String phoneNumber) {
        if (Permissions.callFlow(activity, this)) call(phoneNumber);
        else setInstruction(phoneNumber);
    }

    @Override @RequiresApi(api = Build.VERSION_CODES.M)
    public void onGrant() {
        String nameOrNumber = instruction();
        if (nameOrNumber == null) tryCall();
        else tryCall(nameOrNumber);
    }
}
