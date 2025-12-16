package net.emilla.activity;

import static net.emilla.chime.Chime.START;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import net.emilla.chime.Chime;
import net.emilla.chime.Chimer;
import net.emilla.command.app.AppEntry;
import net.emilla.config.SettingVals;
import net.emilla.util.Apps;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

final class AssistViewModel extends ViewModel {

    /*internal*/ static final class Factory implements ViewModelProvider.Factory {

        private final Context mAppContext;

        /*internal*/ Factory(Context ctx) {
            mAppContext = ctx.getApplicationContext();
        }

        @Override @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new AssistViewModel(mAppContext);
        }

    }

    private final Context mAppContext;

    public final Resources res;
    public final SharedPreferences prefs;

    public final boolean noTitlebar;
    public final boolean alwaysShowData;

    public boolean noCommand = true;
    public boolean dataAvailable = true;
    public boolean dataVisible;
    public boolean dialogOpen = false;
    // todo: you can probably hard-code these UI-state properties into views, fragments, .. directly?

    public int imeAction = EditorInfo.IME_ACTION_NEXT;

    @Nullable
    public final String motd;

    public final AppEntry[] apps;
    @Nullable
    private HashMap<String, ArrayList<Uri>> mAttachmentMap = null;

    private final Chimer mChimer;
    @Nullable
    private EnumSet<Chime> mSuppressedChimes = null;

    @Deprecated // Todo: use what the 'modern' navigation system wants instead of KEYCODE_BACK.
    private boolean mDontTryCancel = false;

    private AssistViewModel(Context appContext) {
        super();

        mAppContext = appContext;

        this.res = appContext.getResources();
        this.prefs = PreferenceManager.getDefaultSharedPreferences(appContext);

        this.noTitlebar = !SettingVals.showTitlebar(this.prefs, this.res);
        this.alwaysShowData = SettingVals.alwaysShowData(prefs);
        this.dataVisible = this.alwaysShowData;

        this.motd = noTitlebar ? null : SettingVals.motd(this.prefs, this.res);

        this.apps = Apps.launchers(appContext.getPackageManager());

        mChimer = Chimer.of(this.prefs);
        mChimer.chime(mAppContext, START);
    }

    public HashMap<String, ArrayList<Uri>> attachmentMap() {
        if (mAttachmentMap == null) {
            mAttachmentMap = new HashMap<String, ArrayList<Uri>>();
        }

        return mAttachmentMap;
    }

    public void suppressChime(Chime chime) {
        if (mSuppressedChimes == null) {
            mSuppressedChimes = EnumSet.of(chime);
        } else {
            mSuppressedChimes.add(chime);
        }
    }

    public void chime(Chime chime) {
        if (mSuppressedChimes == null || !mSuppressedChimes.contains(chime)) {
            mChimer.chime(mAppContext, chime);
        } else {
            mSuppressedChimes.remove(chime);

            if (mSuppressedChimes.isEmpty()) {
                mSuppressedChimes = null;
            }
        }
    }

    public void suppressBackCancellation() {
        mDontTryCancel = true;
    }

    public boolean askTryCancel() {
        if (mDontTryCancel) {
            mDontTryCancel = false;
            return false;
        }
        return true;
    }

}
