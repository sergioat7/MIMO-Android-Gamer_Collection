package es.upsa.mimo.gamercollection.activities;

import android.content.Intent;
import android.os.Bundle;
import es.upsa.mimo.gamercollection.R;
import es.upsa.mimo.gamercollection.activities.base.BaseActivity;
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler;

public class LandingActivity extends BaseActivity {

    private SharedPreferencesHandler sharedPrefHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPrefHandler = new SharedPreferencesHandler(this);

        Class<?> cls;
        Long initTime = System.currentTimeMillis() / 1000;
        if (sharedPrefHandler.isLoggedIn()) {
            cls = MainActivity.class;
        } else {
            cls = LoginActivity.class;
        }
        Long finalTime = System.currentTimeMillis() / 1000;
        Long taskTime = finalTime - initTime;
        Long time = Math.max(0, 1000 - taskTime);

        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }
}
