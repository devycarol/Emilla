package net.emilla;

import static android.content.Intent.*;
import static android.view.KeyEvent.*;
import static android.view.inputmethod.EditorInfo.*;
import static net.emilla.chime.Chimer.*;
import static java.lang.Character.isWhitespace;
import static java.lang.Math.max;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.preference.PreferenceManager;

import net.emilla.chime.Chimer;
import net.emilla.chime.Custom;
import net.emilla.chime.Nebula;
import net.emilla.chime.Redial;
import net.emilla.chime.Silence;
import net.emilla.command.CmdTree;
import net.emilla.command.DataCmd;
import net.emilla.command.EmillaCommand;
import net.emilla.command.EmillaCommand.Commands;
import net.emilla.command.core.View;
import net.emilla.config.ConfigActivity;
import net.emilla.exceptions.EmillaException;
import net.emilla.exceptions.EmlaAppsException;
import net.emilla.settings.SettingVals;
import net.emilla.system.AppEmilla;
import net.emilla.system.EmillaForegroundService;
import net.emilla.utils.Apps;
import net.emilla.utils.Contact;
import net.emilla.utils.Dialogs;
import net.emilla.utils.Features;
import net.emilla.utils.Lang;
import net.emilla.views.ActionButton;

import java.util.ArrayList;
import java.util.List;

public class AssistActivity extends EmillaActivity {
public static final int // intent codes
    GET_FILE = 1,
    GET_PICTURE = 2,
    PICK_VIEW_CONTACT = 3,
    PICK_EDIT_CONTACT = 4;

private SharedPreferences mPrefs;
private CmdTree mCmdTree;
private List<ResolveInfo> mAppList;
private Chimer mChimer;

private LayoutInflater mInflater;
private FrameLayout mEmptySpace;
private TextView mHelpBox;
private EditText mCommandField, mDataField;
private ActionButton mSubmitButton;
private ActionButton mShowDataButton;
private LinearLayout mActionsContainer;
private LinearLayout mFieldsContainer;
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
private boolean mHasTitlebar;

//public static long nanosPlease(long prevTime, String label) {
//    long curTime = System.nanoTime();
//    String s = String.valueOf(curTime - prevTime);
//    StringBuilder sb = new StringBuilder(label).append(": ");
//    int start = sb.length();
//    for (int i = sb.append(s).length() - 3; i > start; i -= 3) sb.insert(i, ',');
//    Log.d("nanosPlease", sb.append(" nanoseconds").toString());
//    return System.nanoTime();
//}

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_assist);

    mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    mChimer = switch (SettingVals.soundSet(mPrefs)) {
        case Chimer.NONE -> new Silence();
        case Chimer.NEBULA -> new Nebula(this);
        case Chimer.VOICE_DIALER -> new Redial();
        case Chimer.CUSTOM -> new Custom(this, mPrefs);
        default -> throw new RuntimeException();
    };
    if (savedInstanceState == null) chime(START);

    Resources res = getResources();

    ActionBar actionBar = getSupportActionBar();
    if (actionBar == null) throw new NullPointerException();
    mHasTitlebar = SettingVals.showTitleBar(mPrefs, res);
    if (mHasTitlebar) {
        String dfltTitle = res.getString(R.string.activity_assistant);
        String title = mPrefs.getString("motd", dfltTitle);
        if (!title.equals(dfltTitle)) actionBar.setTitle(title);
    } else actionBar.hide();

    if (savedInstanceState == null) switch (mPrefs.getString("run_in_background", "follow_system")) {
        case "follow_system":
            // TODO: have listener to turn off the service when power save mode or the accessibility
            //  service is activated
            // also ensure "always on" works as intended
            PowerManager pwrMgr = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (pwrMgr.isPowerSaveMode()) break;
            // fall
        case "always":
            startService(new Intent(this, EmillaForegroundService.class));
    }

    PackageManager pm = getPackageManager();
    mAppList = Apps.resolveList(pm);
    mCmdTree = EmillaCommand.tree(mPrefs, res, pm, mAppList);

    mEmptySpace = findViewById(R.id.empty_space);
    mHelpBox = findViewById(R.id.help_text_box);
    mHelpBox.setVisibility(TextView.GONE);
    mCommandField = findViewById(R.id.field_command);
    mDataField = findViewById(R.id.field_data);
    mSubmitButton = findViewById(R.id.button_submit);
    mShowDataButton = findViewById(R.id.button_show_data);

    setupCommandField();
    boolean alwaysShowData = SettingVals.alwaysShowData(mPrefs);
    // TODO ACC: There's little to no reason for a hidden data field if a screen reader is in use.
    mDataVisible = alwaysShowData
            || savedInstanceState != null && savedInstanceState.getBoolean("dataFieldVisible");
    setupDataField();

    mEmptySpace.setOnClickListener(v -> cancelIfWarranted());
    mHelpBox.setOnClickListener(v -> cancelIfWarranted());

    setupSubmitButton();
    setupShowDataButton(alwaysShowData);
    setupMoreActions();
}

@Override
protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);

    String action = intent.getAction();
    if (mFocused && action != null) switch (action) {
        case ACTION_ASSIST, ACTION_VOICE_COMMAND -> quickAct(mPrefs.getString("action_double_assist",
                Features.torch(getPackageManager()) ? "torch" : "config"));
        // Todo: this is broken for the corner gesture. Seems to be an Android bug (LineageOS 21,
        //  no animations, navbar hell).
    }
}

@Override // Todo: replace with view-model
protected void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putBoolean("dataFieldVisible", mDataVisible);
}

private void setupCommandField() {
    mCommandField.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence text, int start, int count,
                int after) {}

        @Override
        public void onTextChanged(CharSequence text, int start, int before,
                int count) {
            onCommandChanged(text.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {}
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

    mCommand = mCmdTree.newCore(this, Commands.DEFAULT, null);

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
    mSubmitButton.setOnClickListener(v -> submitCommand());
    mSubmitButton.setLongPress(v -> {
        quickAct(mPrefs.getString("action_long_press_submit", "select_all"));
        return true;
    }, R.drawable.ic_select_all); // Todo: icon should be in tandem with the action setting
}

private void cursorStart() {
    if (mCommandField.length() == 0) {
        chime(PEND);
        return;
    }
    if (!mCommandField.hasFocus()) mCommandField.requestFocus();
    int start = mCommandField.getSelectionStart(), end = mCommandField.getSelectionEnd();
    if (max(start, end) == 0) {
        mCommandField.setSelection(mCommandField.length());
        chime(RESUME);
    } else {
        mCommandField.setSelection(0);
        chime(ACT);
    }
}

private void showDataField(boolean focus) {
    mDataField.setVisibility(EditText.VISIBLE);
    if (focus) mDataField.requestFocus();
    mShowDataButton.setIcon(R.drawable.ic_hide_data);
    mShowDataButton.setContentDescription(getString(R.string.spoken_description_hide_data));
    mDataVisible = true;
}

private void disableDataButton() {
    mShowDataButton.setEnabled(false);
    mShowDataButton.setAlpha(0.5f);
}

private void hideDataField() {
    int start, end;
    boolean dataFocused = mDataField.hasFocus();
    if (dataFocused) {
        int dataLen = mDataField.length();
        start = mDataField.getSelectionStart() - dataLen;
        end = mDataField.getSelectionEnd() - dataLen;
    } else {
        start = mCommandField.getSelectionStart();
        end = mCommandField.getSelectionEnd();
    }
    mCommandField.requestFocus();
    if (mDataField.length() > 0) {
        Editable commandText = mCommandField.getText();
        Editable dataText = mDataField.getText();
        int len = commandText.length();
        if (len == 0 || isWhitespace(commandText.charAt(len - 1))) mCommandField.append(dataText);
        else mCommandField.append(Lang.wordConcat(getResources(), "", dataText));
        mDataField.setText(null);
    }
    int newLen = mCommandField.length();
    if (dataFocused) mCommandField.setSelection(newLen + start, newLen + end);
    else mCommandField.setSelection(start, end);
    mDataField.setVisibility(EditText.GONE);
    if (!mDataAvailable) disableDataButton();
    mShowDataButton.setIcon(R.drawable.ic_show_data);
    mShowDataButton.setContentDescription(getString(R.string.spoken_description_show_data));
    mDataVisible = false;
}

private void setupShowDataButton(boolean disable) {
    if (disable) mShowDataButton.setVisibility(ActionButton.GONE);
    else mShowDataButton.setOnClickListener(v -> {
        if (mDataVisible) hideDataField();
        else showDataField(true);
    });
}

public void addAction(@IdRes int actionId, CharSequence description,
        @DrawableRes int iconId, android.view.View.OnClickListener click) {
    ActionButton actionButton = (ActionButton) mInflater.inflate(R.layout.btn_action,
            mActionsContainer, false);
    actionButton.setId(actionId);
    actionButton.setIcon(iconId);
    actionButton.setContentDescription(description);
    actionButton.setOnClickListener(click);
    mActionsContainer.addView(actionButton);
}

private void setupMoreActions() {
    // TODO: save state hell
    mInflater = LayoutInflater.from(this);
    mActionsContainer = findViewById(R.id.container_more_actions);
    addAction(R.id.action_cursor_start, getString(R.string.action_cursor_start),
            R.drawable.ic_cursor_start, v -> cursorStart());
    mFieldsContainer = findViewById(R.id.container_more_fields);
}

public void removeAction(@IdRes int actionId) {
    mActionsContainer.removeView(findViewById(actionId));
}

public boolean toggleField(@IdRes int fieldId, @StringRes int hintId,
        boolean focus) {
    // Todo: it's vague which field is which. First step: make them be ordered consistently.
    EditText field = findViewById(fieldId);
    if (field == null) {
        field = (EditText) mInflater.inflate(R.layout.field_extra, mFieldsContainer, false);
        field.setId(fieldId);
        field.setHint(hintId);
        mFieldsContainer.addView(field);
        if (focus) field.requestFocus();
        return true;
    } else if (field.getVisibility() == EditText.VISIBLE) {
        if (field.hasFocus()) mCommandField.requestFocus();
        // Todo: instead track the previously focused field(s)?
        field.setVisibility(EditText.GONE);
        return false;
    } else {
        field.setVisibility(EditText.VISIBLE);
        if (focus) field.requestFocus();
        return true;
    }
}

public void hideField(@IdRes int fieldId) {
    EditText field = findViewById(fieldId);
    if (field != null) {
        if (field.hasFocus()) mCommandField.requestFocus();
        // Todo: instead track the previously focused field(s)?
        field.setVisibility(EditText.GONE);
    }
}

public String getFieldText(@IdRes int fieldId) {
    EditText field = findViewById(fieldId);
    return field == null || field.getVisibility() == EditText.GONE ? null : field.getText().toString();
}

private void torch() { // todo: migrate out as a command
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return;
    // TODO: https://github.com/LineageOS/android_packages_apps_Torch
    CameraManager camMgr = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
    String getCameraID;
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
    Intent config = Apps.meTask(this, ConfigActivity.class);
    if (shouldCancel()) succeed(config);
    else {
        startActivity(config);
        chime(ACT);
    }
}

private void toggleSelectAll() {
    if (mCommandField.length() != 0) {
        int selStart = mCommandField.getSelectionStart();
        int selEnd = mCommandField.getSelectionEnd();
        int len = mCommandField.length();
        if (selStart != 0 || selEnd != len) refreshInput();
        else {
            mCommandField.setSelection(len, len);
            chime(RESUME);
        }
    }
}

private void quickAct(String action) {
    switch (action) {
        case "torch" -> torch();
        case "config" -> config();
        case "select_all" -> toggleSelectAll();
        default -> chime(PEND);
    }
}

protected void onResume() {
    super.onResume();
    if (!mFocused) {
        if (mLaunched) resume(true);
        else mLaunched = true;
        mFocused = true;
    }
}

@Override
protected void onStop() {
    super.onStop();

    mFocused = false;
    if (!(isChangingConfigurations() || isFinishing())) {
        if (shouldCancel()) cancel();
        else if (!mDialogOpen) chime(PEND);
    }
}

@Override
protected void onDestroy() {
    super.onDestroy();
    Contact.clean();
}

@Override
public boolean onKeyUp(int keyCode, KeyEvent event) {
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

private void updateDataAvailability(boolean available) {
    if (available) {
        enableDataButton();
        mDataField.setEnabled(mDataEnabled = true);
    } else if (!mDataVisible) disableDataButton();
    else mDataField.setEnabled(mDataEnabled = false);
    mDataAvailable = available;
}


public void updateLabel(CharSequence title) {
    if (mHasTitlebar) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setTitle(mNoCommand ? mPrefs.getString("motd",
                getString(R.string.activity_assistant))
            : title);
    }
}

public void updateDetails(int detailsId) {
    CharSequence details = detailsId == -1 ? null : String.join("\n\n", getResources().getStringArray(detailsId));
    if (details == null) mHelpBox.setVisibility(TextView.GONE);
    else {
        mHelpBox.setVisibility(TextView.VISIBLE);
        mHelpBox.setText(details);
    }
}

public void updateDataHint() {
    if (mNoCommand) {
        mDataField.setContentDescription(null);
        mDataField.setHint(R.string.data_hint_default);
    } else if (mCommand.usesData()) {
        mDataField.setContentDescription(null);
        mDataField.setHint(((DataCmd) mCommand).dataHint());
    } else if (mDataField.getHint() != null) {
        mDataField.setHint(null);
        mDataField.setContentDescription(getString(R.string.data_hint_default));
    }
}

public void setImeAction(/*mutable*/ int action) {
    if (mNoCommand) action = IME_ACTION_NEXT;
    if (action != mImeAction) mCommandField.setImeOptions(mImeAction = action);
}

private void onCommandChanged(/*mutable*/ String command) {
    int len = command.length();
    if (len > 0 && command.charAt(0) == ' ') {
        int nonSpace = 0;
        while (len > ++nonSpace && command.charAt(nonSpace) == ' ') ;
        command = command.substring(nonSpace, len);
    }

    EmillaCommand cmd = mCmdTree.get(this, command);
    boolean noCommand = command.isEmpty();
    if (cmd != mCommand || noCommand != mNoCommand) {
        mCommand.clean();
        mCommand = cmd;
        mNoCommand = noCommand;
        cmd.init();

        int iconId = noCommand ? R.drawable.ic_assistant : mCommand.icon();
        mSubmitButton.setIcon(iconId);
        boolean dataAvailable = noCommand || mCommand.usesData();
        if (dataAvailable != mDataAvailable) updateDataAvailability(dataAvailable);
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

public String mediaCsv() {
    return mPrefs.getString("medias", View.DFLT_MEDIA);
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
    Intent in = new Intent(ACTION_GET_CONTENT).setType("*/*")
            .putExtra(EXTRA_ALLOW_MULTIPLE, true);

    if (in.resolveActivity(getPackageManager()) == null) throw new EmlaAppsException("No file selection app found on your device.");
    startActivityForResult(in, GET_FILE);
}

/*====================*
 * Command Processing *
 *====================*/

private void chime(byte id) {
    // Todo: I'd love to add a couple more in-built sound packs from open source ecosystems! Anyone
    //  stumbling across this is welcome to give suggestions.
    mChimer.chime(id);
}

private void resume(boolean chime) {
    if (!mDialogOpen) {
        EditText field = mDataFocused ? mDataField : mCommandField;
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

public void onCloseDialog(boolean chime) {
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

private void showDialog(AlertDialog dialog, byte chime) {
    // TODO: view enablement shouldn't be handled on a view-by-view basis. Perhaps target the mother
    //  of all views (whatever that is) or get to the bottom of why views can be clicked in the
    //  split-second after dialog invocation in the first place
    mDialogOpen = true;
    mEmptySpace.setEnabled(false);
    mSubmitButton.setEnabled(false);
    mCommandField.setEnabled(false);
    mDataField.setEnabled(false);
    dialog.show();
    chime(chime);
}

public void fail(CharSequence message) {
    chime(FAIL);
    toast(message, true);
}

public void fail(AlertDialog dialog) {
    showDialog(dialog, FAIL);
}

public void offer(AlertDialog dialog) {
    showDialog(dialog, PEND);
}

public void offer(Intent intent, int requestCode) {
    startActivityForResult(intent, requestCode); // Todo: rework handling, resolve deprecation
}

@RequiresApi(api = Build.VERSION_CODES.M)
public void offer(String permission, int requestCode) {
    requestPermissions(new String[]{permission}, requestCode);
    chime(PEND);
}

public void succeed(Intent intent) {
    finishAndRemoveTask();
    startActivity(intent);
    chime(SUCCEED);
}

private void submitCommand() {
    String fullCommand = mCommandField.getText().toString().trim();
    if (fullCommand.isEmpty()) {
        quickAct(mPrefs.getString("action_no_command", "config"));
        return;
    }
try {
    if (mCommand.usesData() && mDataField.length() > 0) {
        ((DataCmd) mCommand).execute(mDataField.getText().toString());
    } else mCommand.execute();
} catch (EmillaException e) {
    fail(e.getMessage());
} catch (Exception e) {
    fail(getString(R.string.toast_error_unknown));
    Log.e("UNKNOWN COMMAND ERROR", "", e);
    // Todo: easy bug reporting ;)
}}
}