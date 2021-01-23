package es.upsa.mimo.gamercollection.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import java.util.Locale;
import javax.inject.Inject;
import es.upsa.mimo.gamercollection.R;
import es.upsa.mimo.gamercollection.activities.base.BaseActivity;
import es.upsa.mimo.gamercollection.injection.GamerCollectionApplication;
import es.upsa.mimo.gamercollection.utils.Constants;
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler;

public class LandingActivity extends BaseActivity {

    @Inject
    SharedPreferencesHandler sharedPrefHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((GamerCollectionApplication) getApplication()).appComponent.inject(this);

        createNotificationChannel();

        String language = sharedPrefHandler.getLanguage();
        Configuration conf = getResources().getConfiguration();
        conf.setLocale(new Locale(language));
        getResources().updateConfiguration(conf, getResources().getDisplayMetrics());

        Class<?> cls;
        Long initTime = System.currentTimeMillis() / 1000;
        if (sharedPrefHandler.isLoggedIn()) {
            cls = MainActivity.class;
        } else {
            cls = LoginActivity.class;
        }
        Long finalTime = System.currentTimeMillis() / 1000;
        long taskTime = finalTime - initTime;
        long time = Math.max(0, 1000 - taskTime);

        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    //MARK: - Private functions

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
