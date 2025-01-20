package net.emilla.command.core;

import static android.content.Intent.ACTION_SENDTO;
import static android.content.Intent.EXTRA_SUBJECT;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.action.field.FieldToggle;
import net.emilla.action.field.SubjectField;
import net.emilla.exception.EmlaFeatureException;
import net.emilla.settings.Aliases;
import net.emilla.util.Contacts;
import net.emilla.util.Features;

import java.util.HashMap;

public class Sms extends CoreDataCommand {

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

    private void launchMessenger(Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !Features.sms(pm())) {
            throw new EmlaFeatureException(NAME, R.string.error_feature_sms);
            // TODO: handle at install—don't store in sharedprefs in case of settings sync/transfer
            //  It's also possible that this check isn't necessary.
        }
        String subject = mSubjectToggle.fieldText();
        appSucceed(subject == null ? intent : intent.putExtra(EXTRA_SUBJECT, subject));
        // TODO: I've not gotten this to work yet
    }

    @NonNull
    private Intent withMsg(Intent intent, String message) {
        return intent.putExtra("sms_body", message);
        // overwrites any existing draft to the recipient TODO: detect, warn, confirm.
    }

    @NonNull
    private Intent withRecipients(Intent intent, String recipients) {
        return intent.setData(Uri.parse("smsto:" + Contacts.namesToPhones(recipients, mPhoneMap)));
    }

    @Override
    protected void run() {
        launchMessenger(mIntent);
    }

    @Override
    protected void run(@NonNull String recipients) {
        launchMessenger(withRecipients(mIntent, recipients));
    }

    @Override
    protected void runWithData(@NonNull String message) {
        launchMessenger(withMsg(mIntent, message));
    }

    @Override
    protected void runWithData(@NonNull String recipients, @NonNull String message) {
        launchMessenger(withMsg(withRecipients(mIntent, recipients), message));
    }
}
