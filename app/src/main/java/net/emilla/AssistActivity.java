package net.emilla;

import static android.content.Intent.*;
import static android.view.KeyEvent.*;
import static android.view.inputmethod.EditorInfo.*;
import static net.emilla.utils.Chime.*;
import static java.lang.Character.isWhitespace;
import static java.lang.Math.max;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.preference.PreferenceManager;

import net.emilla.commands.CommandTree;
import net.emilla.commands.CommandView;
import net.emilla.commands.CommandWrapDefault;
import net.emilla.commands.DataCommand;
import net.emilla.commands.EmillaCommand;
import net.emilla.commands.EmillaCommand.Commands;
import net.emilla.config.ConfigActivity;
import net.emilla.exceptions.EmillaException;
import net.emilla.exceptions.EmlaAppsException;
import net.emilla.settings.SettingVals;
import net.emilla.system.AppEmilla;
import net.emilla.system.EmillaForegroundService;
import net.emilla.utils.Apps;
import net.emilla.utils.Chime;
import net.emilla.utils.Contacts;
import net.emilla.utils.Dialogs;
import net.emilla.utils.Features;
import net.emilla.utils.Lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AssistActivity extends EmillaActivity implements OnSharedPreferenceChangeListener {
public static final int // intent codes
    GET_FILE = 1,
    GET_PICTURE = 2,
    PICK_VIEW_CONTACT = 3,
    PICK_EDIT_CONTACT = 4;

private SharedPreferences mPrefs;

private CommandTree mCommandTree;
private List<ResolveInfo> mAppList;
private HashMap<String, String> mPhoneMap;
private HashMap<String, String> mEmailMap;
private String mSounds;
private ToneGenerator mToneGenerator;

private FrameLayout mEmptySpace;
private TextView mHelpBox;
private EditText mCommandField, mDataField;
private ImageButton mSubmitButton, mShowDataButton, mCursorStartButton;

private AlertDialog mCancelDialog;

private ArrayList<Uri> mAttachments;
private EmillaCommand mCommand;
private boolean mNoCommand = true;
private boolean
    mDataAvailable = true,
    mDataEnabled = true,
    mDataVisible = false,
    mDataFocused = false;
private int mImeAction = IME_ACTION_NEXT;
private boolean mLaunched = false;
private boolean mFocused = false;
private boolean mPendingCancel = false;
private boolean mDialogOpen = false;

private boolean mTouchingSubmit = false;
private boolean mLongPressingSubmit = false;
private int mCommandIcon = R.drawable.ic_assistant;
private boolean mHasTitlebar;

//public static long nanosPlease(final long prevTime, final String label) {
//    final long curTime = System.nanoTime();
//    final String s = String.valueOf(curTime - prevTime);
//    final StringBuilder sb = new StringBuilder(label).append(": ");
//    final int start = sb.length();
//    for (int i = sb.append(s).length() - 3; i > start; i -= 3) sb.insert(i, ',');
//    Log.d("nanosPlease", sb.append(" nanoseconds").toString());
//    return System.nanoTime();
//}

@Override
protected void onCreate(final Bundle savedInstanceState) {
    mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    mSounds = SettingVals.soundSet(mPrefs);
    if (mSounds.equals("voice_dialer")) mToneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC,
            ToneGenerator.MAX_VOLUME);
    if (savedInstanceState == null) chime(START);
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_assist);
    final Resources res = getResources();

    final ActionBar actionBar = getSupportActionBar();
    if (actionBar == null) throw new NullPointerException();
    mHasTitlebar = SettingVals.showTitleBar(mPrefs, res);
    if (mHasTitlebar) {
        final String dfltTitle = res.getString(R.string.activity_assistant);
        final String title = mPrefs.getString("motd", dfltTitle);
        if (!title.equals(dfltTitle)) actionBar.setTitle(title);
    } else actionBar.hide();

    if (savedInstanceState == null) switch (mPrefs.getString("run_in_background", "follow_system")) {
        case "follow_system":
            // TODO: have listener to turn off the service when power save mode or the accessibility
            //  service is activated
            // also ensure "always on" works as intended
            final PowerManager pwrMgr = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (pwrMgr.isPowerSaveMode()) break;
            // fall
        case "always":
            startService(new Intent(this, EmillaForegroundService.class));
    }

    final PackageManager pm = getPackageManager();
    mAppList = Apps.resolveList(pm);
    mCommandTree = EmillaCommand.tree(mPrefs, res, pm, mAppList);
    mPhoneMap = Contacts.mapPhones(mPrefs);
    mEmailMap = Contacts.mapEmails(mPrefs);

    mEmptySpace = findViewById(R.id.empty_space);
    mHelpBox = findViewById(R.id.help_text_box);
    mHelpBox.setVisibility(TextView.GONE);
    mCommandField = findViewById(R.id.field_command);
    mDataField = findViewById(R.id.field_data);
    mSubmitButton = findViewById(R.id.button_submit);
    mShowDataButton = findViewById(R.id.button_show_data);

    setupCommandField();
    final boolean alwaysShowData = SettingVals.alwaysShowData(mPrefs);
    mDataVisible = alwaysShowData
            || savedInstanceState != null && savedInstanceState.getBoolean("dataFieldVisible");
    setupDataField();

    mEmptySpace.setOnClickListener(v -> cancelIfWarranted());
    mHelpBox.setOnClickListener(v -> cancelIfWarranted());

    setupSubmitButton();
    setupShowDataButton(alwaysShowData);
    setupCursorStartButton();
}

@Override // Todo: replace with view-model
protected void onSaveInstanceState(@NonNull final Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putBoolean("dataFieldVisible", mDataVisible);
}

private void setupCommandField() {
    mCommandField.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(final CharSequence text, final int start, final int count,
                final int after) {}

        @Override
        public void onTextChanged(final CharSequence text, final int start, final int before,
                final int count) {
            setTextsIfCommandChanged(text.toString());
        }

        @Override
        public void afterTextChanged(final Editable s) {}
    });

    mCommandField.setOnEditorActionListener((v, actionId, event) -> switch (actionId) {
        case IME_ACTION_UNSPECIFIED, IME_ACTION_NONE, IME_ACTION_PREVIOUS -> false;
        default -> switch (mImeAction) {
            // TODO ACC: There must be clarity on what the enter key will do if you can't see the
            //  screen.
            case IME_ACTION_NEXT:
                if (mDataAvailable) {
                    showDataField(true);
                    yield true;
                }
                // fall
            case IME_ACTION_GO, IME_ACTION_SEARCH, IME_ACTION_SEND, IME_ACTION_DONE:
                submitCommand();
                yield true;
            default:
                yield false;
        };
    });

    mCommand = mCommandTree.newCore(this, Commands.DEFAULT);

    mCommandField.setHorizontallyScrolling(false);
    mCommandField.setMaxLines(8);
    mCommandField.requestFocus();
}

private void setupDataField() {
    mDataField.setOnFocusChangeListener((v, hasFocus) -> {
        if (!mDialogOpen) mDataFocused = hasFocus;
    });
    if (mDataVisible) showDataField(false);
}

private void setupSubmitButton() {
    mSubmitButton.setOnLongClickListener(v -> {
        if (mTouchingSubmit) {
            // TODO ACC: verify accessibility function
            mSubmitButton.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            mLongPressingSubmit = true;
            mSubmitButton.setImageResource(R.drawable.ic_assistant);
        }
        return false;
    });
    mSubmitButton.setOnTouchListener((v, event) -> {
        // TODO ACC: it's critical to ensure this works with accessibility services
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN: // Todo: you shouldn't be able to submit twice
            mSubmitButton.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            mTouchingSubmit = true;
            return false;
        case MotionEvent.ACTION_MOVE:
            if (mTouchingSubmit
                    && (0.0f > event.getX() || (int) event.getX() > v.getWidth()
                        || 0.0f > event.getY() || (int) event.getY() > v.getHeight())) {
                mTouchingSubmit = false;
                if (mLongPressingSubmit) {
                    final int hapticType = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R ? HapticFeedbackConstants.GESTURE_END
                            : HapticFeedbackConstants.KEYBOARD_TAP;
                    mSubmitButton.performHapticFeedback(hapticType);
                    mSubmitButton.setImageResource(mCommandIcon);
                    mLongPressingSubmit = false;
                }
                return true;
            }
            return super.onTouchEvent(event);
        case MotionEvent.ACTION_UP:
            if (!mTouchingSubmit) return super.onTouchEvent(event);
            mTouchingSubmit = false;
            if (mLongPressingSubmit) {
                mLongPressingSubmit = false;
                quickAct(mPrefs.getString("action_long_press_submit", "select_all"));
                mSubmitButton.setImageResource(mCommandIcon);
            } else submitCommand();
            mSubmitButton.setImageResource(mCommandIcon);
            // fallthrough
        default:
            return super.onTouchEvent(event);
        }
    });
}

private void cursorStart() {
    if (mCommandField.length() == 0) {
        chime(PEND);
        return;
    }
    if (!mCommandField.hasFocus()) mCommandField.requestFocus();
    final int start = mCommandField.getSelectionStart(), end = mCommandField.getSelectionEnd();
    if (max(start, end) == 0) {
        mCommandField.setSelection(mCommandField.length());
        chime(RESUME);
    } else {
        mCommandField.setSelection(0);
        chime(ACT);
    }
}

private void showDataField(final boolean focus) {
    mDataField.setVisibility(EditText.VISIBLE);
    if (focus) mDataField.requestFocus();
    mShowDataButton.setImageResource(R.drawable.ic_hide_data);
    mShowDataButton.setContentDescription(getString(R.string.spoken_description_hide_data));
    mDataVisible = true;
}

private void disableDataButton() {
    mShowDataButton.setEnabled(false);
    mShowDataButton.setAlpha(0.5f);
}

private void hideDataField() {
    final int start, end;
    final boolean dataFocused = mDataField.hasFocus();
    if (dataFocused) {
        final int dataLen = mDataField.length();
        start = mDataField.getSelectionStart() - dataLen;
        end = mDataField.getSelectionEnd() - dataLen;
    } else {
        start = mCommandField.getSelectionStart();
        end = mCommandField.getSelectionEnd();
    }
    mCommandField.requestFocus();
    if (mDataField.length() > 0) {
        final Editable commandText = mCommandField.getText();
        final Editable dataText = mDataField.getText();
        final int len = commandText.length();
        if (len == 0 || isWhitespace(commandText.charAt(len - 1))) mCommandField.append(dataText);
        else mCommandField.append(Lang.wordConcat(getResources(), "", dataText));
        mDataField.setText(null);
    }
    final int newLen = mCommandField.length();
    if (dataFocused) mCommandField.setSelection(newLen + start, newLen + end);
    else mCommandField.setSelection(start, end);
    mDataField.setVisibility(EditText.GONE);
    if (!mDataAvailable) disableDataButton();
    mShowDataButton.setImageResource(R.drawable.ic_show_data);
    mShowDataButton.setContentDescription(getString(R.string.spoken_description_show_data));
    mDataVisible = false;
}

private void setupShowDataButton(final boolean disable) {
    if (disable) mShowDataButton.setVisibility(ImageButton.GONE);
    else mShowDataButton.setOnClickListener(v -> {
        if (mDataVisible) hideDataField();
        else showDataField(true);
    });
}

private void setupCursorStartButton() {
    if (true) {
        // Todo: instant-command buttons of all sorts
        findViewById(R.id.container_more_actions).setVisibility(LinearLayout.GONE);
        return;
    }
    mCursorStartButton = findViewById(R.id.button_cursor_start);
    mCursorStartButton.setOnClickListener(v -> cursorStart());
}

private void torch() { // todo: migrate out as a command
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return;
    // TODO: https://github.com/LineageOS/android_packages_apps_Torch
    final CameraManager camMgr = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
    final String getCameraID;
try {
    getCameraID = camMgr.getCameraIdList()[0];
    if (AppEmilla.sTorching) {
        camMgr.setTorchMode(getCameraID, false);
        chime(RESUME);
        // TODO ACC: if you can't see the torch, this feedback is critically insufficient.
        AppEmilla.sTorching = false;
    } else {
        camMgr.setTorchMode(getCameraID, true);
        chime(ACT);
        // TODO ACC: if you can't see the torch, this feedback is critically insufficient.
        AppEmilla.sTorching = true;
    }
} catch (CameraAccessException ignored) {} // Torch not toggled, nothing to do.
}

private void config() {
    final Intent config = Apps.meTask(this, ConfigActivity.class);
    if (shouldCancel()) succeed(config);
    else {
        startActivity(config);
        chime(ACT);
    }
}

private void toggleSelectAll() {
    if (mCommandField.length() != 0) {
        final int selStart = mCommandField.getSelectionStart();
        final int selEnd = mCommandField.getSelectionEnd();
        final int len = mCommandField.length();
        if (selStart != 0 || selEnd != len) refreshInput();
        else {
            mCommandField.setSelection(len, len);
            chime(RESUME);
        }
    }
}

private void quickAct(final String action) {
    switch (action) {
        case "torch" -> torch();
        case "config" -> config();
        case "select_all" -> toggleSelectAll();
        default -> chime(PEND);
    }
}

protected void onResume() {
    super.onResume();
    // TODO: Must revise trigger. Double-acts for the corner gesture in no-buttons mode. Doesn't
    //  act for the access menu "Assistant" item
    if (mFocused && !mDialogOpen /*Todo: this is weird..*/) {
        final String doubleAssistAction = mPrefs.getString("action_double_assist",
                Features.torch(getPackageManager()) ? "torch" : "config");
        quickAct(doubleAssistAction);
    } else {
        if (mLaunched) resume(true);
        else mLaunched = true;
        mFocused = true;
    }
}

@Override
public void onWindowFocusChanged(final boolean hasFocus) {
    if (!hasFocus) mFocused = false;
}

@Override
protected void onStop() {
    super.onStop();

    if (!(isChangingConfigurations() || isFinishing())) {
        if (shouldCancel()) cancel();
        else if (!mDialogOpen) chime(PEND);
    }
}

@Override
public boolean onKeyUp(final int keyCode, final KeyEvent event) {
    // todo: configurable action for second tap of the 'assist' key, could type/run a favorite command (like torch :), pull up a gadget of favorites, lots of possibilities.
    // u could have the command chain into a second literal "cancel" command to just have a double-click action :)
    // could have it open a new window or tab. tabs would be cool. anyways, assist key can't be captured here.
    switch (keyCode) {
    case KEYCODE_BACK -> { // Todo config? command history?
        if (mPendingCancel) cancel(); // todo: is this necessary, or is it already handled by the dialog's key event listener?
        else cancelIfWarranted();
    }
    case KEYCODE_MENU -> quickAct(mPrefs.getString("action_menu", "config"));
    case KEYCODE_SEARCH -> refreshInput(); // Todo config
    default -> { return false; }
    }
    return true;
}

private void enableDataButton() {
    mShowDataButton.setEnabled(true);
    mShowDataButton.setAlpha(1.0f);
}

private void updateDataAvailability(final boolean available) {
    if (available) {
        enableDataButton();
        mDataField.setEnabled(mDataEnabled = true);
    } else if (!mDataVisible) disableDataButton();
    else mDataField.setEnabled(mDataEnabled = false);
    mDataAvailable = available;
}

private void setTextsIfCommandChanged(/*mutable*/ String command) {
    final int len = command.length();
    if (len > 0 && command.charAt(0) == ' ') {
        int nonSpace = 0;
        while (len > ++nonSpace && command.charAt(nonSpace) == ' ') ;
        command = command.substring(nonSpace, len);
    }

    final EmillaCommand cmd = mCommandTree.get(this, command.toLowerCase());
    final boolean noCommand = command.isEmpty();
    if (cmd != mCommand || noCommand != mNoCommand) {
        mCommand = cmd;
        mNoCommand = noCommand;
        final Resources res = getResources();
        if (mHasTitlebar) {
            final ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                final CharSequence title = noCommand ? mPrefs.getString("motd",
                        res.getString(R.string.activity_assistant))
                    : mCommand.title();
                actionBar.setTitle(title);
            }
        }
        final int detailsId = mCommand.detailsId();
        final CharSequence details = detailsId == -1 ? null : String.join("\n\n", res.getStringArray(detailsId));
        if (details == null) mHelpBox.setVisibility(TextView.GONE);
        else {
            mHelpBox.setVisibility(TextView.VISIBLE);
            mHelpBox.setText(details);
        }

        if (noCommand) {
            mDataField.setContentDescription(null);
            mDataField.setHint(R.string.data_hint_default);
        } else if (mCommand.usesData()) {
            mDataField.setContentDescription(null);
            mDataField.setHint(((DataCommand) mCommand).dataHint());
        } else if (mDataField.getHint() != null) {
            mDataField.setHint(null);
            mDataField.setContentDescription(res.getString(R.string.data_hint_default));
        }

        final int iconId = noCommand ? R.drawable.ic_assistant : mCommand.icon();
        mSubmitButton.setImageResource(iconId); // todo: relocate
        mCommandIcon = iconId;
        final boolean dataAvailable = noCommand || mCommand.usesData();
        if (dataAvailable != mDataAvailable) updateDataAvailability(dataAvailable);

        int imeAction = noCommand ? IME_ACTION_NEXT : mCommand.imeAction();
        if (imeAction != mImeAction) {
            mCommandField.setImeOptions(imeAction);
            mImeAction = imeAction;
        }
    }
}

/*=================*
 * Result handling *
 *=================*/

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    // Todo: re-work this entirely. Would like to pass ready-to-go intents whenever possible

    toast("I'm here now!", false);
    switch (requestCode) {
    case GET_FILE -> {
        if (resultCode == RESULT_OK) {
            mAttachments = new ArrayList<>();
            ClipData cd;
            if ((cd = data.getClipData()) == null) mAttachments.add(data.getData());
            else for (int i = 0; i < cd.getItemCount(); ++i) mAttachments.add(cd.getItemAt(i).getUri());
            submitCommand();
        } else toast("Files weren't selected.", false);
    }
    case PICK_VIEW_CONTACT -> {
        if (resultCode == RESULT_OK) succeed(Apps.newTask(ACTION_VIEW, data.getData()));
        // TODO: we must resolve that activity at mapping time
        else toast("A contact wasn't selected.", false);
    }
    case PICK_EDIT_CONTACT -> {
        // todo: i wish that the exit button actually freakin EXITED! - com.android.contacts
        //  similar deal with calendar. make a PR or find some other way to enforce it.
        if (resultCode == RESULT_OK) succeed(Apps.newTask(ACTION_EDIT, data.getData()));
        // TODO: we must resolve that activity at mapping time
        else toast("A contact wasn't selected.", false);
    }}
}

/*================*
 * Getter methods *
 *================*/

public SharedPreferences prefs() {
    return mPrefs;
}

public List<ResolveInfo> appList() {
    return mAppList;
}

public HashMap<String, String> phoneMap() {
    return mPhoneMap;
}

public HashMap<String, String> emailMap() {
    return mEmailMap;
}

public String mediaCsv() {
    return mPrefs.getString("medias", CommandView.DFLT_MEDIA);
}

public ArrayList<Uri> attachments() {
    return mAttachments;
}

/*================*
 * Setter methods *
 *================*/

public void nullifyAttachments() {
    mAttachments = null;
}

/*================*
 * Helper methods *
 *================*/

public void getFiles() { // todo: make private?
    final Intent in = new Intent(ACTION_GET_CONTENT).setType("*/*")
            .putExtra(EXTRA_ALLOW_MULTIPLE, true);

    if (in.resolveActivity(getPackageManager()) == null) throw new EmlaAppsException("No file selection app found on your device.");
    startActivityForResult(in, GET_FILE);
}

/*====================*
 * Command Processing *
 *====================*/

private void chime(final byte id) {
    // Todo: I'd love to add a couple more in-built sound packs from open source ecosystems! Anyone
    //  stumbling across this is welcome to give suggestions.
    switch (mSounds) {
    case Chime.NONE -> {}
    case Chime.NEBULA -> {
        // Todo: still encountering occasional sound cracking issues
        final MediaPlayer player = MediaPlayer.create(this, Chime.nebula(id));
        player.setVolume(0.5f, 0.5f); // todo: adjust the sound resources directly and remove this but maybe make volume configurable
        player.setOnCompletionListener(MediaPlayer::release);
        player.start();
    }
    case Chime.VOICE_DIALER -> mToneGenerator.startTone(Chime.dialerTone(id));
    case Chime.CUSTOM -> {
        final String uriStr = mPrefs.getString(Chime.preferenceOf(id), null);
        MediaPlayer player;
        if (uriStr == null) player = MediaPlayer.create(this, Chime.nebula(id));
        else {
            player = MediaPlayer.create(this, Uri.parse(uriStr));
            if (player == null) player = MediaPlayer.create(this, Chime.nebula(id));
            // In case the URI breaks
        }
        player.setOnCompletionListener(MediaPlayer::release);
        player.start();
    }}
}

private void resume(final boolean chime) {
    if (!mDialogOpen) {
        final EditText field = mDataFocused ? mDataField : mCommandField;
        field.requestFocus();
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                .showSoftInput(field, InputMethodManager.SHOW_IMPLICIT);
        if (chime) chime(RESUME);
    }
}

public void refreshInput() {
    mCommandField.selectAll();
    chime(ACT);
}

private boolean shouldCancel() {
    return mCommandField.length() + mDataField.length() == 0;
}

private void cancel() {
    chime(EXIT);
    finishAndRemoveTask();
}

public void onCloseDialog(final boolean chime) {
    mEmptySpace.setEnabled(true);
    mSubmitButton.setEnabled(true);
    mCommandField.setEnabled(true);
    mDataField.setEnabled(mDataEnabled);
    mDialogOpen = false;

    resume(chime);
}

private void declineCancel() {
    mPendingCancel = false;
    onCloseDialog(true);
}

private void cancelIfWarranted() {
    if (shouldCancel()) cancel();
    else {
        mPendingCancel = true;
        if (mCancelDialog == null) {
            mCancelDialog = Dialogs.okCancelMsg(this, R.string.dialog_exit, R.string.dlg_msg_exit,
                    (dialog, id) -> cancel())
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> declineCancel())
                    .setOnCancelListener(dialog -> declineCancel())
                    .create();
            mCancelDialog.setOnKeyListener((dialog, keyCode, keyEvent) -> {
                if (keyCode == KEYCODE_BACK && keyEvent.getAction() == ACTION_UP) {
                    cancel();
                    return true;
                }
                return false;
            });
        }
        offer(mCancelDialog);
    }
}

private void showDialog(final AlertDialog dialog, final byte chime) {
    mDialogOpen = true;
    mEmptySpace.setEnabled(false);
    mSubmitButton.setEnabled(false);
    mCommandField.setEnabled(false);
    mDataField.setEnabled(false);
    dialog.show();
    chime(chime);
}

public void fail(final CharSequence message) {
    chime(FAIL);
    toast(message, true);
}

public void fail(final AlertDialog dialog) {
    showDialog(dialog, FAIL);
}

public void offer(final AlertDialog dialog) {
    showDialog(dialog, PEND);
}

public void offer(final Intent intent, final int requestCode) {
    startActivityForResult(intent, requestCode); // Todo: rework handling, resolve deprecation
}

@RequiresApi(api = Build.VERSION_CODES.M)
public void offer(final String permission, final int requestCode) {
    requestPermissions(new String[]{permission}, requestCode);
    chime(PEND);
}

public void succeed(final Intent intent) {
    finishAndRemoveTask();
    startActivity(intent);
    chime(SUCCEED);
}

private void submitCommand() {
    final String fullCommand = mCommandField.getText().toString().trim(), data = mDataField.getText().toString();
    if (fullCommand.isEmpty()) {
        quickAct(mPrefs.getString("action_no_command", "config"));
        return;
    }
    final String instruction;
    if (mCommand instanceof CommandWrapDefault) instruction = fullCommand;
    else {
        final int spaceIdx = fullCommand.indexOf(' ');
        // TODO LANG: space-separation no good
        instruction = spaceIdx > 0 ? fullCommand.substring(spaceIdx).trim() : null;
    }
try {
    if (mDataEnabled && mDataField.length() > 0) {
        if (instruction == null) ((DataCommand) mCommand).runWithData(data);
        else ((DataCommand) mCommand).runWithData(instruction, data);
    } else if (instruction == null) mCommand.run();
    else mCommand.run(instruction);
} catch (EmillaException e) {
    fail(e.getMessage());
} catch (Exception e) {
    fail(getString(R.string.toast_error_unknown));
    Log.e("UNKNOWN COMMAND ERROR", "", e);
    // Todo: easy bug reporting ;)
}}

@Override
public void onSharedPreferenceChanged(final SharedPreferences prefs, @Nullable final String key) {
    if (key != null) switch (key) {
        case "phones" -> mPhoneMap = Contacts.mapPhones(prefs);
        case "emails" -> mEmailMap = Contacts.mapEmails(prefs);
    }
}
}