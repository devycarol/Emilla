package net.emilla.config;

import android.os.Build;
import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import net.emilla.AssistActivity;
import net.emilla.EmillaActivity;
import net.emilla.R;
import net.emilla.databinding.ActivityConfigBinding;
import net.emilla.utils.Apps;

public class ConfigActivity extends EmillaActivity {
private ActivityConfigBinding binding;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    binding = ActivityConfigBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    binding.navView.setOnItemReselectedListener(item -> {
        final boolean assistantItem = item.getItemId() == R.id.navigation_assistant;
        if (assistantItem) startActivity(Apps.meTask(this, AssistActivity.class));
    });
    final NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
            .findFragmentById(R.id.nav_host_fragment_activity_config);
    if (navHostFragment == null) return;
    final NavController navController = navHostFragment.getNavController();
    final AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
            R.id.navigation_commands, R.id.navigation_assistant, R.id.navigation_settings)
            .build();
    NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    NavigationUI.setupWithNavController(binding.navView, navController);
}

@Override
public void onBackPressed() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && getSupportFragmentManager().getBackStackEntryCount() == 0) finishAndRemoveTask();
    else super.onBackPressed();
}
}