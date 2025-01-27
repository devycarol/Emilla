package net.emilla.command.core;

import static android.content.Intent.ACTION_SENDTO;
import static android.content.Intent.EXTRA_SUBJECT;

import android.content.Intent;
import android.net.Uri;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.action.field.FieldToggle;
import net.emilla.action.field.SubjectField;
import net.emilla.content.receive.PhoneReceiver;
import net.emilla.settings.Aliases;
import net.emilla.util.Contacts;
import net.emilla.util.Dialogs;

import java.util.HashMap;

public class Sms extends CoreDataCommand implements PhoneReceiver {

    public static final String ENTRY = "sms";
    @StringRes
    public static final int NAME = R.string.command_sms;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_sms;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static Yielder yielder() {
        return new Yielder(true, Sms::new, ENTRY, NAME, ALIASES);
    }

    private static class SmsParams extends CoreDataParams {

        private SmsParams() {
            super(NAME,
                  R.string.instruction_phone,
                  false, // the initials "SMS" shouldn't be lowercased
                  R.drawable.ic_sms,
                  R.string.summary_sms,
                  R.string.manual_sms,
                  R.string.data_hint_message);
        }
    }

    private final Intent mIntent = new Intent(ACTION_SENDTO, Uri.parse("smsto:"));
    private final HashMap<String, String> mPhoneMap;
    private FieldToggle mSubjectToggle;

    public Sms(AssistActivity act) {
        super(act, new SmsParams());
        mPhoneMap = Contacts.mapPhones(act.prefs());
    }

    @Override
    protected void onInit() {
        super.onInit();

        if (mSubjectToggle == null) mSubjectToggle = new SubjectField(activity);
        else if (mSubjectToggle.activated()) reshowField(SubjectField.FIELD_ID);
        giveAction(mSubjectToggle);
        // Todo: attachments
    }

    @Override
    protected void onClean() {
        super.onClean();

        removeAction(SubjectField.ACTION_ID);
        hideField(SubjectField.FIELD_ID);
    }

    @Override
    protected void run() {
        tryMessage(null);
    }

    @Override
    protected void run(@NonNull String recipients) {
        tryMessage(recipients, null);
    }

    private void tryMessage(@Nullable String message) {
        message("", message);
    }

    @Override
    protected void runWithData(@NonNull String message) {
        tryMessage(message);
    }

    @Override
    protected void runWithData(@NonNull String recipients, @NonNull String message) {
        // todo: immediate texting. likely requires a feature check and special permissions.
        //  attachments, feedback for delivered/not delivered..
        tryMessage(recipients, message);
    }

    private void tryMessage(@NonNull String recipients, @Nullable String message) {
        String numbers = Contacts.namesToPhones(recipients, mPhoneMap);
        if (numbers == null && Contacts.isPhoneNumbers(recipients)) numbers = recipients;

        if (numbers != null) message(numbers, message);
        else {
            String toNumbers = Contacts.phonewordsToNumbers(recipients);
            String msg = string(R.string.notice_sms_not_numbers, recipients, toNumbers);
            // todo: better message.
            offerDialog(Dialogs.dual(activity, NAME, msg, R.string.message_directly,
                    (dlg, which) -> message(toNumbers, message)));
        }
    }

    private void message(@NonNull String numbers, @Nullable String message) {
        Intent sendTo = new Intent(ACTION_SENDTO, Uri.parse("smsto:" + numbers));

        if (message != null) sendTo.putExtra("sms_body", message);
        // overwrites any existing draft to the recipient
        // Todo: detect, warn, confirm.

        String subject = mSubjectToggle.fieldText();
        if (subject != null) sendTo.putExtra(EXTRA_SUBJECT, subject);
        // TODO: I've not gotten this to work yet

        appSucceed(sendTo);
    }

    @Override
    public void provide(@NonNull String phoneNumber) {
        message(phoneNumber, activity.dataText());
    }
}
