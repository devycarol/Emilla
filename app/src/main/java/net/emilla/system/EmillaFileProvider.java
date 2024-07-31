package net.emilla.system;

import androidx.core.content.FileProvider;

import net.emilla.R;

public class EmillaFileProvider extends FileProvider {
    public EmillaFileProvider() {
        super(R.xml.paths);
    }
}
