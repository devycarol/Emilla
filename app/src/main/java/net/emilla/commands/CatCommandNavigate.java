package net.emilla.commands;

import static android.content.Intent.ACTION_VIEW;
import static android.content.Intent.CATEGORY_APP_MAPS;

import android.content.Intent;
import android.net.Uri;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.DrawableRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exceptions.EmlaAppsException;
import net.emilla.utils.Apps;

public class CatCommandNavigate extends CatCommand {
private final Intent mIntent = Apps.newTask(ACTION_VIEW);

@Override @DrawableRes
public int icon() {
    return R.drawable.ic_navigate;
}

@Override
public int imeAction() {
    return EditorInfo.IME_ACTION_SEARCH;
}

public CatCommandNavigate(AssistActivity act, String instruct) {
    super(act, instruct, CATEGORY_APP_MAPS, R.string.command_navigate, R.string.instruction_location);
}

@Override
protected void noSuchApp() {
    throw new EmlaAppsException("No maps app found for your device.");
}

@Override
protected void run(String location) {
    // Todo: location bookmarks, navigate to contacts' addresses
    if (mIntent.setData(Uri.parse("geo:0,0?q=" + location)).resolveActivity(packageManager()) == null) throw new EmlaAppsException("No app found for web search."); // todo handle at mapping
    succeed(mIntent);
}
}
