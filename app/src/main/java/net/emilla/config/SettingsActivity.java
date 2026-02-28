package net.emilla.config;

import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import net.emilla.R;

public final class SettingsActivity extends AppCompatActivity {
    public SettingsActivity() {
        super(R.layout.activity_settings);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            EdgeToEdge.enable(this);
        }

        Toolbar titlebar = findViewById(R.id.settings_titlebar);
        setSupportActionBar(titlebar);

        var host = (NavHostFragment) getSupportFragmentManager()
            .findFragmentById(R.id.settings_nav_host)
        ;
        var controller = host.getNavController();
        NavigationUI.setupActionBarWithNavController(this, controller);
    }
}
