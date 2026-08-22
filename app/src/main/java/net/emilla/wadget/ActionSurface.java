package net.emilla.wadget;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;

import net.emilla.Feedback;
import net.emilla.activity.AssistActivity;

public interface ActionSurface {
//    ActionSurface FULL_ASSISTANT = ;
//    ActionSurface BUTTON_AND_TEXT = ;
//    ActionSurface BUTTON = ;
//    ActionSurface THIN_AIR = ;
//    ActionSurface BACKGROUND = ;

    Context getContext();
    @Deprecated
    AssistActivity getAssistActivity();
    Resources getResources();
    SharedPreferences getSharedPreferences();
    ContentResolver getContentResolver();
    PackageManager getPackageManager();

    // TODO: remove
    void take(Feedback feedback);

}
