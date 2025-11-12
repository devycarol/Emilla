package net.emilla.config;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.databinding.ActivityConfigBinding;
import net.emilla.util.Intents;

public final class ConfigActivity extends AppCompatActivity {

    public ConfigActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        var binding = ActivityConfigBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.navView.setOnItemReselectedListener(item -> {
            if (item.getItemId() == R.id.nav_assistant) {
                startActivity(Intents.me(this, AssistActivity.class));
            }
        });

        FragmentManager manager = getSupportFragmentManager();
        var host = (NavHostFragment)
            manager.findFragmentById(R.id.host_fragment);
        if (host == null) {
            throw new IllegalStateException("Couldn't find the host fragment");
        }

        var navController = host.getNavController();
        var configuration = new AppBarConfiguration.Builder(
            R.id.nav_commands,
            R.id.nav_assistant,
            R.id.nav_settings
        ).build();

        NavigationUI.setupActionBarWithNavController(this, navController, configuration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }
}