package net.emilla.wadget;

import android.content.Context;
import android.content.res.Resources;

import net.emilla.Feedback;

public interface ActionSurface {
//    ActionSurface FULL_ASSISTANT = ;
//    ActionSurface BUTTON_AND_TEXT = ;
//    ActionSurface BUTTON = ;
//    ActionSurface THIN_AIR = ;
//    ActionSurface BACKGROUND = ;

    Context getContext();
    Resources getResources();

    // TODO: remove
    void take(Feedback feedback);
}
