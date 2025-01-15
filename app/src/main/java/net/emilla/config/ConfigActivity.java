package net.emilla.config;

import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import net.emilla.AssistActivity;
import net.emilla.EmillaActivity;
import net.emilla.R;
import net.emilla.databinding.ActivityConfigBinding;
import net.emilla.util.Apps;

public class ConfigActivity extends EmillaActivity {

    private ActivityConfigBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityConfigBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.navView.setOnItemReselectedListener(item -> {
            boolean assistantItem = item.getItemId() == R.id.nav_assistant;
            if (assistantItem) startActivity(Apps.meTask(this, AssistActivity.class));
        });
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_config);
        if (navHostFragment == null) return;
        NavController navController = navHostFragment.getNavController();
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_commands, R.id.nav_assistant, R.id.nav_settings)
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }
}