package es.upsa.mimo.gamercollection.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;

import java.util.Locale;

import es.upsa.mimo.gamercollection.R;
import es.upsa.mimo.gamercollection.activities.base.BaseActivity;
import es.upsa.mimo.gamercollection.utils.Constants;
import es.upsa.mimo.gamercollection.viewmodelfactories.LandingViewModelFactory;
import es.upsa.mimo.gamercollection.viewmodels.LandingViewModel;

public class LandingActivity extends BaseActivity {

    //MARK: - Private properties

    private LandingViewModel viewModel;

    // MARK: - Lifecycle methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeUI();
    }

    //MARK: - Private methods

    private void initializeUI() {

        viewModel = new ViewModelProvider(this, new LandingViewModelFactory(getApplication())).get(LandingViewModel.class);
        setupBindings();

        configLanguage();
        createNotificationChannel();

        viewModel.checkVersion();
        viewModel.checkTheme();
    }

    private void setupBindings() {

        viewModel.getLandingClassToStart().observe(this, cls -> {

            Intent intent = new Intent(this, cls);
            startActivity(intent);
        });
    }

    private void configLanguage() {

        String language = viewModel.getLanguage();
        Configuration conf = getResources().getConfiguration();
        conf.setLocale(new Locale(language));
        getResources().updateConfiguration(conf, getResources().getDisplayMetrics());
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(Constants.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) notificationManager.createNotificationChannel(channel);
        }
    }
}
