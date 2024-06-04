package net.emilla.system;

import android.app.Application;

public class AppEmilla extends Application {
    public static boolean sTorching = false;
    // TODO: replace this with a query and delete this class - this isn't fully reliable for
    //  detecting toggle state. e.g. what if the torch was turned on before the app was started?
}
