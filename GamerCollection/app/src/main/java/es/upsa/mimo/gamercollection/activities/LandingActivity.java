package es.upsa.mimo.gamercollection.activities;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.Locale;
import es.upsa.mimo.gamercollection.R;
import es.upsa.mimo.gamercollection.activities.base.BaseActivity;
import es.upsa.mimo.gamercollection.utils.Constants;
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler;

public class LandingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createNotificationChannel();
        requestPermissions();

        SharedPreferencesHandler sharedPrefHandler = new SharedPreferencesHandler(this);

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
            String name = getString(R.string.CHANNEL_NAME);
            String description = getString(R.string.CHANNEL_DESCRIPTION);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(Constants.channelId, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) notificationManager.createNotificationChannel(channel);
        }
    }

    private void requestPermissions() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY}, 0);
                }
            }
        }
    }
}
