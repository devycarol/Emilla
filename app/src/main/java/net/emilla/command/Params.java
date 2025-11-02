package net.emilla.command;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

public interface Params {

    /// The command's name in Title Case.
    ///
    /// @param res can be used to retrieve the name from string resources.
    /// @return the name of the command.
    String name(Resources res);

    /// The command's title as it should appear in the assistant's action-bar. Usually, this
    /// should be the command name followed by a brief description of what it takes as input.
    ///
    /// @param res can be used to retrieve the title from string resources.
    /// @return the command's slightly detailed title.
    CharSequence title(Resources res);

    /// The command's icon for the submit button.
    ///
    /// @param ctx can be used to retrieve the icon from drawable resources.
    /// @return the command's icon drawable.
    Drawable icon(Context ctx);

}
